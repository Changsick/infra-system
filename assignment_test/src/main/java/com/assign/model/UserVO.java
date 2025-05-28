package com.assign.model;

import java.util.Date;

import com.assign.constant.UserConstant.UserLevelEnum;
import com.assign.dto.request.SignupRequestDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class UserVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "user_level")
	@Enumerated(EnumType.STRING)
	private UserLevelEnum userLevel;
	
	@Column(name = "created_at")
    private Date createdAt;
	
	@Column(name = "last_change_password")
	private Date lastChangePassword;
	
	@Column(name = "lastLogin")
	private Date lastLogin;
	
	@Builder
	public UserVO(Long userId, String email, String password, String username, UserLevelEnum userLevel,
			Date createdAt, Date lastChangePassword, Date lastLogin) {
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.username = username;
		this.userLevel = userLevel;
		this.createdAt = createdAt;
		this.lastChangePassword = lastChangePassword;
		this.lastLogin = lastLogin;
	}
	
	@Builder(builderClassName = "signupBuilder", builderMethodName = "signupBuilder")
	public UserVO(SignupRequestDTO signup, String password) {
		this.email = signup.getEmail();
		this.password = password;
		this.username = signup.getUsername();
		this.userLevel = UserLevelEnum.ROLE_USER;
		this.createdAt = new Date();
		this.lastChangePassword = new Date();
	}
	
}
