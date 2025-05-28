package com.assign.dto;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResultWrapperDTO<T> {

	private T data;
	private HttpStatus status;
	
	@Builder
	public ResultWrapperDTO(T data, HttpStatus status) {
		this.data = data;
		this.status = status;
	}
}
