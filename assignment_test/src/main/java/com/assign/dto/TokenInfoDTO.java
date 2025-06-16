package com.assign.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TokenInfoDTO {

	private String token;
	private Long iat;
	private Long exp;
	private String refreshToken;
	
	@Builder
	public TokenInfoDTO(String token, Long iat, Long exp, String refreshToken) {
		this.token = token;
		this.iat = iat;
		this.exp = exp;
	}
}
