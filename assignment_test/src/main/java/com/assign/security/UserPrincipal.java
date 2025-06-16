package com.assign.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.assign.model.UserVO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
	
	private final String email;
	private final String password;
	private final Collection<GrantedAuthority> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	public static UserPrincipal createPrincipal(UserVO userVO) {
		
		return new UserPrincipal(userVO.getEmail()
				, userVO.getPassword()
				, Collections.singletonList(new SimpleGrantedAuthority(userVO.getUserLevel().name())));
	}
}
