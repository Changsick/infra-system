package com.assign.constant;

public class UserConstant {
	
	public static final String EXIST_EMAIL_USER_MSG = "This is the email address of an already existing user.";
	
	public static final String USER_NOT_FOUND = "User not found";
	
	public static final String PASSWORD_EXPIRED = "Password expired";
	
	public static final String INVALID_PASSWORD = "Invalid password";
	
	public static final String EXPIRED_TOKEN = "expired token";
	
	public static final String JWT_PREFIX = "Bearer ";

	public enum UserLevelEnum {
		ROLE_USER,
		ROLE_ADMIN,
		inscector
	}
	
	public enum AspectMethodEnum {
		SIGNINUSER,
		SIGNOUTUSER
	}
	
	public enum UserActivityInfoEnum {
		LOGIN,
		LOGOUT
	}
	
	public enum ResultEnum {
		SUCCESS,
		FAIL
	}
}
