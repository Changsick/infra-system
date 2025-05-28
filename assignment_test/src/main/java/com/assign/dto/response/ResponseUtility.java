package com.assign.dto.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class ResponseUtility {

	@JsonSerialize
    public static class EmptyJsonResponse {}
	
	public static <T> ResponseEntity<ResponseDataDTO<T>> createGetSuccessResponse(T data) {
        return createSuccessResponse(data, HttpStatus.OK);
    }
	
	public static <T> ResponseEntity<ResponseDataDTO<T>> createPostSyncSuccessResponse(T data) {
        return createSuccessResponse(data, HttpStatus.CREATED);
    }
	
		
	public static <T> ResponseEntity<ResponseDataDTO<T>> createLoginSuccessResponse(T data, String accessToken, String refreshToken) {
		if (data == null) data = (T) new EmptyJsonResponse();
		
		ResponseDataDTO<T> resData = ResponseDataDTO.<T>builder()
				.data(data)
				.build();
		HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Refresh-Token", refreshToken);
		return new ResponseEntity<>(resData, headers, HttpStatus.CREATED);
	}

    public static <T> ResponseEntity<ResponseDataDTO<T>> createSuccessResponse(T data, HttpStatus httpStatus) {
    	if (data == null) data = (T) new EmptyJsonResponse();
    	
    	ResponseDataDTO<T> resData = ResponseDataDTO.<T>builder()
                .data(data)
                .build();
        return new ResponseEntity<>(resData, httpStatus);
    }
    
    public static ResponseEntity<ResponseDataDTO<Void>> createFailResponse(HttpStatus httpStatus) {
        return new ResponseEntity<>(ResponseDataDTO.<Void>builder()
                .build(), httpStatus);
    }

}
