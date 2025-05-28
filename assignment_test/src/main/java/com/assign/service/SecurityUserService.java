package com.assign.service;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.assign.model.UserVO;
import com.assign.repository.UserRepository;
import com.assign.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

//@Service
//@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

//	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// unique emial을 기준으로 한다.
//		UserVO user = userRepository.findByEmail(email)
//				.orElseThrow(() -> new UsernameNotFoundException("Can not find User by Email"));
//		
//		return new org.springframework.security.core.userdetails.User(
//				user.getUsername(),
//				user.getPassword(),
//				Collections.singleton(() -> user.getUserLevel().name()) // ROLE_XXX
//				);
//		return UserPrincipal.createPrincipal(user);
		return null;
	}

}
