package com.assign.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.assign.dto.ActivityInfoDTO;
import com.assign.dto.response.ResponseDataDTO;
import com.assign.dto.response.ResponseUtility;
import com.assign.exception.BusinessException;
import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.model.mongodb.UserLogMongo;
import com.assign.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminRestController {
	
	private final UserService userService;
	
	@Operation(
            summary = "사용자 로그 조회",
            method = "GET",
            description = "사용자 로그 조회",
            parameters = {
                @Parameter(name = "userId", example = "17", description = "pathVariable"),
            })
	@GetMapping("/users/{userId}/logs")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<Page<ActivityInfoDTO>>> getUserLog(
			@PathVariable("userId") Long userId
			, @RequestParam(name = "startDate") Long startDate
			, @RequestParam(name = "endDate") Long endDate
			, @RequestParam(name = "infoType", required = false) String infoType
			, @RequestParam(name = "resultType", required = false) String resultType
			, @RequestParam(name = "page", defaultValue = "1") Integer page
            , @RequestParam(name = "size",defaultValue = "10") Integer size
//            , @RequestParam(required = false) String sort
			){
		Pageable pageable = PageRequest.of(page, size);
		return ResponseUtility.createGetSuccessResponse(userService.getUserLog(
				userId, startDate, endDate, infoType, resultType, pageable
				));
	}
	
	@Operation(
            summary = "특정 사용자 강제 로그아웃",
            method = "POST",
            description = "특정 사용자를 강제로 로그아웃 시킨다.",
            parameters = {
                @Parameter(name = "userId", example = "17", description = "pathVariable"),
            })
	@PostMapping("/users/{userId}/expire-tokens")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<Map>> expireTokens(@PathVariable("userId") Long userId) throws BusinessException {
		return ResponseUtility.createPostSyncSuccessResponse(userService.expireTokens(userId));
	}
	
	@Operation(
			summary = "사용자 로그 조회 - ElasticSearch",
            method = "GET",
            description = "사용자 로그 조회",
            parameters = {
                @Parameter(name = "userId", example = "17", description = "pathVariable"),
            })
	@GetMapping("/users/{userId}/logs/elastic")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<Page<UserLogEntry>>> getUserLogElasticSearch(
			@PathVariable(name = "userId") Integer userId, 
			@RequestParam(name = "page", defaultValue = "0") int page, 
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "infoType", required = false) String infoType, 
            @RequestParam(name = "resultType", required = false) String resultType, 
            @RequestParam(name = "start") Long start, 
            @RequestParam(name = "end") Long end){
		log.info("getUserLogElasticSearch invoked");
		Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		return ResponseUtility.createGetSuccessResponse(userService.getUserLogElasticSearch(
				userId, 
	            infoType, 
	            resultType, 
	            start, 
	            end,
				pageRequest));
	}
	
	@Operation(
            summary = "엘라스틱 서치 초기화",
            method = "GET",
            description = "엘라스틱서치 초기화")
	@GetMapping("/users/elasticrefresh")
	@ResponseBody
	public String elasticrefresh(){
		userService.elasticrefresh();
		return "success";
	}
	
}
