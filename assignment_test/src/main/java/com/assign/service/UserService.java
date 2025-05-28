package com.assign.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.assign.dto.ActivityInfoDTO;
import com.assign.dto.TokenInfoDTO;
import com.assign.dto.UserDTO;
import com.assign.dto.request.SignRequestDTO;
import com.assign.dto.request.SignupRequestDTO;
import com.assign.exception.BusinessException;
import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.model.mongodb.UserLogMongo;

import reactor.core.publisher.Mono;

public interface UserService {

	UserDTO signupUser(SignupRequestDTO signRequestDTO) throws BusinessException, Exception;
	
	TokenInfoDTO signinUser(SignRequestDTO signRequestDTO) throws BusinessException;

	Map signoutUser(String token, String refreshToken) throws BusinessException;

	Map expireTokens(Long userId);

	Page<ActivityInfoDTO> getUserLog(Long userId, Long startDate, Long endDate, String infoType, String resultType, Pageable pageable);

	Page<UserLogEntry> getUserLogElasticSearch(
			Integer userId, 
            String infoType, 
            String resultType, 
            Long start, 
            Long end,
			Pageable pageRequest);

	String elasticrefresh();

	Mono<Page<UserLogMongo>> getUserLogMongo(Integer userId, String infoType, String resultType, Long start, Long end,
			Pageable pageRequest);
	
	void saveUserLogElasticsearch(UserLogEntry logEntry);
	
	void saveUserLogMongo(UserLogMongo logMongo);

	TokenInfoDTO refreshAccessToken(String refreshToken);
}
