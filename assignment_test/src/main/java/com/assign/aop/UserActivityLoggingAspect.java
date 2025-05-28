package com.assign.aop;

import java.time.LocalDateTime;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;
import com.assign.dto.KafkaRequestDTO;
import com.assign.dto.TokenInfoDTO;
import com.assign.dto.request.SignRequestDTO;
import com.assign.jwt.JwtTokenProvider;
import com.assign.kafka.KafkaProducer;
import com.assign.model.UserActivityInfoVO;
import com.assign.model.UserVO;
import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.model.mongodb.UserLogMongo;
import com.assign.repository.UserActivityInfoRepository;
import com.assign.repository.UserRepository;
import com.assign.repository.elasticsearch.LogRepository;
import com.assign.repository.mongodb.LogMongoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class UserActivityLoggingAspect {

	private final UserActivityInfoRepository userActivityInfoRepository;
	
	private final UserRepository userRepository;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	private final KafkaProducer kafkaProducer;
	
//private final LogRepository logRepository;
//	
//	private final LogMongoRepository logMongoRepository;
	
    @Around("execution(* com.assign.service.UserServiceImpl.signinUser(..))"
    		+ " || execution(* com.assign.service.UserServiceImpl.signoutUser(..))")
    public Object logLoginLogout(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 이름을 얻는다 (login, logout)
        String action = joinPoint.getSignature().getName().toUpperCase();
        UserActivityInfoVO userActivityInfoVO = UserActivityInfoVO.builder().build();
//        UserLogEntry logEntry = UserLogEntry.builder().build();
        UserLogEntry logEntry = new UserLogEntry();
        UserLogMongo logMongo = UserLogMongo.builder().build();
        
        Object[] reqData = joinPoint.getArgs();
        try {
            // 메서드 실행
            Object result = joinPoint.proceed();
            userActivityInfoVO.setResultType(ResultEnum.SUCCESS);
            logEntry.setResultType(ResultEnum.SUCCESS.name());
            logMongo.setResultType(ResultEnum.SUCCESS.name());

            // 성공적인 로그인 또는 로그아웃 처리
            saveActivityInfo(userActivityInfoVO, action, reqData, result, logEntry, logMongo);
            
            return result;
        } catch (Exception ex) {
            // 예외가 발생하면 실패로 처리
        	userActivityInfoVO.setResultType(ResultEnum.FAIL);
        	userActivityInfoVO.setReason(ex.getMessage());
        	logEntry.setResultType(ResultEnum.FAIL.name());
        	logEntry.setReason(ex.getMessage());
        	logMongo.setResultType(ResultEnum.FAIL.name());
        	logMongo.setReason(ex.getMessage());
        	
        	saveActivityInfo(userActivityInfoVO, action, reqData, null, logEntry, logMongo);
       		throw ex; // 예외를 다시 던져서 정상적인 예외 처리 흐름을 유지
        } 
    }
    
    private void saveActivityInfo(UserActivityInfoVO userActivityInfoVO, 
    		String action, Object[] data, Object result, UserLogEntry logEntry,
    		UserLogMongo logMongo) {
    	String email = null;
    	switch (action) {
			case "SIGNINUSER" -> {
				userActivityInfoVO.setInfoType(UserActivityInfoEnum.LOGIN);
				logEntry.setInfoType(UserActivityInfoEnum.LOGIN.name());
				logMongo.setInfoType(UserActivityInfoEnum.LOGIN.name());
				SignRequestDTO reqData = (SignRequestDTO)data[0];
				email = reqData.getEmail();
				if(userActivityInfoVO.getResultType().equals(ResultEnum.SUCCESS)) {
					TokenInfoDTO tokenInfo = (TokenInfoDTO) result;
					userActivityInfoVO.setLoginToken(tokenInfo.getToken());
					logEntry.setLoginToken(tokenInfo.getToken());
					logMongo.setLoginToken(tokenInfo.getToken());
				}
			}
			case "SIGNOUTUSER" -> {
				userActivityInfoVO.setInfoType(UserActivityInfoEnum.LOGOUT);
				logEntry.setInfoType(UserActivityInfoEnum.LOGOUT.name());
				logMongo.setInfoType(UserActivityInfoEnum.LOGOUT.name());
				String token = (String)data[0];
				email = jwtTokenProvider.getUsername(token);
			}
    	}
    	
    	Optional<UserVO> userOpt = userRepository.findByEmail(email);
    	
    	if(!userOpt.isPresent()) {
    		log.error("can not found useId from email -> " + email);
    		return;
    	}
    	
    	userActivityInfoVO.setUserId(userOpt.get().getUserId());
    	logEntry.setUserId(userOpt.get().getUserId());
    	logMongo.setUserId(userOpt.get().getUserId());
    	userActivityInfoRepository.save(userActivityInfoVO);
    	logEntry.setCreatedAt(LocalDateTime.now());
    	logMongo.setCreatedAt(LocalDateTime.now());
//    	logRepository.save(logEntry);
//    	logMongoRepository.save(logMongo);
//    	log.info("save logEntry : " + logEntry);
//    	log.info("save logMongo : " + logMongo);
    	sendElasticKafka(logEntry);
    	sendMongoKafka(logMongo);
    }
    
    private void sendElasticKafka(UserLogEntry logEntry) {
    	try {			
    		kafkaProducer.sendMessage(KafkaRequestDTO.builder()
    				.topic("log-elastic-topic")
    				.payload(logEntry)
    				.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void sendMongoKafka(UserLogMongo logMongo) {
    	try {			
    		kafkaProducer.sendMessage(KafkaRequestDTO.builder()
    				.topic("log-mongo-topic")
    				.payload(logMongo)
    				.build());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
}
