package com.assign.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.assign.dto.TokenInfoDTO;
import com.assign.dto.UserDTO;
import com.assign.dto.request.SignRequestDTO;
import com.assign.dto.request.SignupRequestDTO;
import com.assign.dto.response.ResponseDataDTO;
import com.assign.dto.response.ResponseUtility;
import com.assign.exception.BusinessException;
import com.assign.security.UserPrincipal;
import com.assign.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User Controller", description = "User CRUD API 엔드포인트")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserRestController {
	
	private final UserService userService;
	
	@Operation(
            summary = "회원가입",
            method = "POST",
            description = "email, pw, 이름을 기재"
//            parameters = {
//                @Parameter(name = "nftId", example = "1", description = "nft table PK"),
//                @Parameter(name = "itemNumber", example = "1", description = "nft item table PK")
//            }
            )
	@PostMapping("/signup")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<UserDTO>> signup(
			@RequestBody @Valid SignupRequestDTO signUpData) throws BusinessException, Exception {
		return ResponseUtility.createPostSyncSuccessResponse(
				userService.signupUser(signUpData));
	}
	
	@Operation(
            summary = "로그인",
            method = "POST",
            description = "email, pw 기재"
            )
	@PostMapping("/signin")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<TokenInfoDTO>> signin(
			@RequestBody @Valid SignRequestDTO signUpData) throws BusinessException {
		TokenInfoDTO info = userService.signinUser(signUpData);
		return ResponseUtility.createLoginSuccessResponse(userService.signinUser(signUpData), info.getToken(), info.getRefreshToken());
	}
	
	@Operation(
            summary = "엑세스 토큰 재발급",
            method = "POST",
            description = "헤더에 Refresh-Token을 넣는다."
            )
	@PostMapping("/refresh")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<TokenInfoDTO>> refreshAccessToken(
			@RequestHeader("Refresh-Token") String refreshToken) throws BusinessException {
		TokenInfoDTO info = userService.refreshAccessToken(refreshToken);
		return ResponseUtility.createPostSyncSuccessResponse(info);
	}
	
	@Operation(
            summary = "로그아웃",
            method = "POST",
            description = "헤더에 엑세스 토큰 필요"
            )
	@PostMapping("/signout")
	@ResponseBody
	public ResponseEntity<ResponseDataDTO<Map>> signout(@RequestHeader("Authorization") String token, 
			@RequestHeader("Refresh-Token") String refreshToken) throws BusinessException {
		return ResponseUtility.createPostSyncSuccessResponse(
				userService.signoutUser(token, refreshToken));
	}
	
	@Operation(
            summary = "jwt테스트용",
            method = "get",
            description = ""
            )
	@GetMapping("/authTest")
	@ResponseBody
	public String authTest() throws BusinessException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info(authentication.getName());
		log.info(""+authentication.getPrincipal());
		log.info("auth : "+authentication.getAuthorities());
		log.info("UserDetails : " + (authentication.getDetails() instanceof UserDetails));
		log.info("UserPrincipal : " + (authentication.getDetails() instanceof UserPrincipal));
		
		return "SUCCESS";
	}
	
}
