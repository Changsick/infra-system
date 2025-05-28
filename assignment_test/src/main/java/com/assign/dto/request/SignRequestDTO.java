package com.assign.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class SignRequestDTO {
	
	@Email(message = "Invalid email format")	
	@NotBlank
	private String email;
	
	@NotBlank
	@Size(min = 6, max = 20, message = "password must be between 6 and 20 characters")
	private String password;
	
}
