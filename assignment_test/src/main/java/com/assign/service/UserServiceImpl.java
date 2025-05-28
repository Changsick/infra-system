package com.assign.service;

import static com.assign.constant.UserConstant.EXIST_EMAIL_USER_MSG;
import static com.assign.constant.UserConstant.EXPIRED_TOKEN;
import static com.assign.constant.UserConstant.INVALID_PASSWORD;
import static com.assign.constant.UserConstant.PASSWORD_EXPIRED;
import static com.assign.constant.UserConstant.USER_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;
import com.assign.dto.ActivityInfoDTO;
import com.assign.dto.TokenInfoDTO;
import com.assign.dto.UserDTO;
import com.assign.dto.request.SignRequestDTO;
import com.assign.dto.request.SignupRequestDTO;
import com.assign.exception.BusinessException;
import com.assign.jwt.JwtTokenProvider;
import com.assign.model.UserActivityInfoVO;
import com.assign.model.UserVO;
import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.model.mongodb.UserLogMongo;
import com.assign.repository.ActivityInfoRepositoryImpl;
import com.assign.repository.UserActivityInfoRepository;
import com.assign.repository.UserRepository;
import com.assign.repository.elasticsearch.LogBoolNativeRepository;
import com.assign.repository.elasticsearch.LogCriteriaRepository;
import com.assign.repository.elasticsearch.LogRepository;
import com.assign.repository.mongodb.LogMongoCriteriaRepository;
import com.assign.repository.mongodb.LogMongoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository userRepository;
	
	private final UserActivityInfoRepository activityInfoRepository;
	
	private final PasswordEncoder passwordEncoder;
	
    private final JwtTokenProvider jwtTokenProvider;
    
    private final ActivityInfoRepositoryImpl activityInfoRepositoryImpl;
    
    private final ElasticsearchOperations elasticsearchOperations;
    
    private final LogBoolNativeRepository logBoolNativeRepository;
    
    private final LogCriteriaRepository logCriteriaRepository;
    
    private final LogMongoCriteriaRepository logMongoCriteriaRepository;
    
    private final LogRepository logRepository;
	
	private final LogMongoRepository logMongoRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVO user = userRepository.findByEmail(username)
		.orElseThrow(() -> new UsernameNotFoundException("Can not find User by Email"));

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				Collections.singleton(() -> user.getUserLevel().name()) // ROLE_XXX
				);
	}
	
	@Override
	@Transactional
	public UserDTO signupUser(SignupRequestDTO signRequestDTO) throws BusinessException, Exception {
		Optional<UserVO> checkUser = userRepository.findByEmail(signRequestDTO.getEmail());
		if(checkUser.isPresent()) {
			throw new BusinessException(EXIST_EMAIL_USER_MSG, HttpStatus.BAD_REQUEST);
		}
		UserVO signupUser = UserVO.signupBuilder()
									.signup(signRequestDTO)
									.password(passwordEncoder.encode(signRequestDTO.getPassword()))
									.build();
		return UserDTO.byUserVOBuilder()
				.userVO(userRepository.save(signupUser))
				.build();
	}

	@Override
	@Transactional
	public TokenInfoDTO signinUser(SignRequestDTO signRequestDTO) throws BusinessException {
		HttpStatus failStatus = HttpStatus.UNAUTHORIZED;
		String email = signRequestDTO.getEmail();
		String password = signRequestDTO.getPassword();
        UserVO user = userRepository.findByEmail(email)
        		.orElseThrow(() -> new BusinessException(USER_NOT_FOUND, failStatus));

        Date passwordLastModifiedDate = user.getLastLogin();

        if (isPasswordExpired(passwordLastModifiedDate)) {
            throw new BusinessException(PASSWORD_EXPIRED, failStatus);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(INVALID_PASSWORD, failStatus);
        }

        user.setLastLogin(new Date());
        userRepository.save(user);
        
        TokenInfoDTO tokenInfo = jwtTokenProvider.createToken(email, Arrays.asList(user.getUserLevel().name()));
        
        String refreshId = UUID.randomUUID().toString();
        String refreshToken = jwtTokenProvider.createRefreshToken(email, refreshId);
        jwtTokenProvider.saveRefreshToken(refreshToken);
        tokenInfo.setRefreshToken(refreshToken);
        
        log.info("Login Success");
        return tokenInfo;
	}
	
	private boolean isPasswordExpired(Date passwordLastModifiedDate) {
        if (passwordLastModifiedDate == null) return false;
        LocalDate now = LocalDate.now();
        LocalDate pm = passwordLastModifiedDate.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate();
        long diffDays = ChronoUnit.DAYS.between(pm, now);

        return diffDays >= 90; // 비밀번호가 90일 이상 경과하면 만료
    }
	
	@Override
	public TokenInfoDTO refreshAccessToken(String refreshToken) {
		HttpStatus failStatus = HttpStatus.UNAUTHORIZED;
		
		if(!jwtTokenProvider.validateToken(refreshToken)){
			throw new BusinessException("Invalid refresh token", failStatus);
		}
		
		if(!jwtTokenProvider.isValidRefreshToken(refreshToken)){
			throw new BusinessException("Invalid refresh token", failStatus);
		}
		
		String email = jwtTokenProvider.getUsername(refreshToken);
		
		UserVO user = userRepository.findByEmail(email)
        		.orElseThrow(() -> new BusinessException(USER_NOT_FOUND, failStatus));
		
		TokenInfoDTO tokenInfo = jwtTokenProvider.createToken(email, Arrays.asList(user.getUserLevel().name()));
		tokenInfo.setRefreshToken(refreshToken);
		return tokenInfo;
	}

	@Override
	public Map<String, Boolean> signoutUser(String token, String refreshToken) throws BusinessException {
		if (token == null || !jwtTokenProvider.validateToken(token)) {
			throw new BusinessException(EXPIRED_TOKEN, HttpStatus.UNAUTHORIZED);
		}
		jwtTokenProvider.blacklistToken(token);
		jwtTokenProvider.deleteRefreshToken(refreshToken);
		return Map.of("success", true);
	}

	@Override
	public Map<String, Boolean> expireTokens(Long userId) {
		
		LocalDateTime localDateTime = LocalDateTime.now().minusHours(1);

        // LocalDateTime을 Date로 변환
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Date targetDate = Date.from(zonedDateTime.toInstant());
        
        // elasticsearch나 mongodb에서 읽어와서 블랙리스트 & refresh delete / elasticsearch, mongodb의 log 관련 refreshToken 필드 추가 필요
		List<UserActivityInfoVO> infoList = activityInfoRepository.findByUserIdAndInfoTypeAndCreatedAtGreaterThanEqual(
				userId, UserActivityInfoEnum.LOGIN, targetDate);
		
		if(infoList != null && infoList.size() > 0) {
			infoList.stream()
				.forEach(info -> {
					String token = info.getLoginToken();
					if (token == null || !jwtTokenProvider.validateToken(token)) {
						log.info("expried token");
						return;
					}
					jwtTokenProvider.blacklistToken(token);
				});
		}
		
		return Map.of("success", true);
	}

	@Override
	public Page<ActivityInfoDTO> getUserLog(Long userId, Long startDate, Long endDate, String infoType,
			String resultType, Pageable pageable) {
		Date startParam = new Date(startDate);
		Date endParam = new Date(endDate);
		UserActivityInfoEnum infoEnum = null;
		ResultEnum resultEnum = null;
		if(infoType != null) {
			if(UserActivityInfoEnum.LOGIN.name().equals(infoType)) {				
				infoEnum = UserActivityInfoEnum.LOGIN;
			}else{
				infoEnum = UserActivityInfoEnum.LOGOUT;				
			}
		}
		
		if(resultType != null) {			
			if(resultEnum.SUCCESS.name().equals(resultType)) {				
				resultEnum = resultEnum.SUCCESS;
			}else{
				resultEnum = resultEnum.FAIL;
			}
		}
		
		return activityInfoRepositoryImpl.findInfoData(userId, infoEnum, resultEnum, startParam, endParam, pageable);
	}

	@Override
	public Page<UserLogEntry> getUserLogElasticSearch(
			Integer userId, 
            String infoType, 
            String resultType, 
            Long start, 
            Long end,
			Pageable pageable) {
		
		// Cariteria ver
//		return logCriteriaRepository.logPagingCriteriaQuery(userId, infoType, resultType, start, end, pageable);
		
		// boolquery(native) ver
		return logBoolNativeRepository.logPagingNativeQuery(userId, infoType, resultType, start, end, pageable);
	}
	
	@Override
	public String elasticrefresh() {
		elasticsearchOperations.indexOps(UserLogEntry.class).delete();
		elasticsearchOperations.indexOps(UserLogEntry.class).create();
		return null;
	}

	@Override
	public Mono<Page<UserLogMongo>> getUserLogMongo(Integer userId, String infoType, String resultType, Long start, Long end,
			Pageable pageRequest) {
		// TODO Auto-generated method stub
		return logMongoCriteriaRepository.logPagingCriteriaQuery(userId, infoType, resultType, start, end, pageRequest);
	}

	@Override
	public void saveUserLogElasticsearch(UserLogEntry logEntry) {
		// TODO Auto-generated method stub
		logRepository.save(logEntry);
	}

	@Override
	public void saveUserLogMongo(UserLogMongo logMongo) {
		// TODO Auto-generated method stub
		logMongoRepository.save(logMongo).subscribe(); 
	}

}
