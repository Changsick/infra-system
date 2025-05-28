package com.assign.dto;

import com.assign.model.UserVO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDTO {

	private String username;
	
	private String email;
	
	private Long createdAt;

	@Builder(builderClassName = "byUserVOBuilder", builderMethodName = "byUserVOBuilder")
	public UserDTO(UserVO userVO) {
		this.username = userVO.getUsername();
		this.email = userVO.getEmail();
		this.createdAt = userVO.getCreatedAt().getTime();
	}
}
