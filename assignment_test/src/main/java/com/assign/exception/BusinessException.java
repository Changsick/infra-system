package com.assign.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
    private HttpStatus statusCode;
    
	public BusinessException(Throwable cause, HttpStatus statusCode) {
		super(cause);
		this.statusCode = statusCode;
		this.message = cause.getMessage();
	}
	
	public BusinessException(String message, HttpStatus statusCode) {
		this.statusCode = statusCode;
		this.message = message;
	}
	
}
