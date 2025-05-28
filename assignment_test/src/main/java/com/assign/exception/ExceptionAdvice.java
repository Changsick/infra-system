package com.assign.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
	
	@ExceptionHandler(BusinessException.class)
    protected ResponseEntity<String> handleException(
            BusinessException businessException) {
		businessException.printStackTrace();
        return new ResponseEntity<>(businessException.getMessage(), businessException.getStatusCode());
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<String> handleIllegalArgumentException(
			IllegalArgumentException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map> handleException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
		 Map<String, String> errors = new HashMap<>();
		methodArgumentNotValidException.printStackTrace();
		methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(error ->
		      errors.put(error.getField(), error.getDefaultMessage())
		  );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
	
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//            errors.put(error.getField(), error.getDefaultMessage())
//        );
//        return errors;
//    }
	
//	@ExceptionHandler(Exception.class)
//	protected ResponseEntity<String> handleException(
//			Exception exception) {
//		exception.printStackTrace();
//		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
//	}
	
	
}