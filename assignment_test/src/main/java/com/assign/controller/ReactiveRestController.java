package com.assign.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.assign.dto.response.ResponseDataDTO;
import com.assign.model.mongodb.UserLogMongo;
import com.assign.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reactive")
@Slf4j
public class ReactiveRestController {

	private final UserService userService;
	
	@Operation(
			summary = "사용자 로그 조회 - MongoDB",
            method = "GET",
            description = "사용자 로그 조회",
            parameters = {
                @Parameter(name = "userId", example = "17", description = "pathVariable"),
            })
	@GetMapping("/users/{userId}/logs/mongo")
	@ResponseBody
	public Mono<ResponseEntity<ResponseDataDTO<Object>>> getUserLogMongo( // ResponseEntity<ResponseDataDTO<Page<UserLogMongo>>>
			@PathVariable(name = "userId") Integer userId, 
			@RequestParam(name = "page", defaultValue = "0") int page, 
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "infoType", required = false) String infoType, 
			@RequestParam(name = "resultType", required = false) String resultType, 
			@RequestParam(name = "start") Long start, 
			@RequestParam(name = "end") Long end){
		log.info("getUserLogMongo invoked");
		Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		Mono<Page<UserLogMongo>> result = userService.getUserLogMongo(
				userId, 
				infoType, 
				resultType, 
				start, 
				end,
				pageRequest);
		return result
				.map(data -> ResponseEntity.ok(ResponseDataDTO.builder().data(data).build()))
				.doOnError(e -> e.printStackTrace())
				.onErrorResume(e -> Mono.just(ResponseEntity.internalServerError().build()))
				;
	}
	
	@Operation(summary = "reactive test", method = "GET")
	@GetMapping("/hello")
	public Mono<String> greet() {		
		return Mono.just("hello");
	}
	
}
