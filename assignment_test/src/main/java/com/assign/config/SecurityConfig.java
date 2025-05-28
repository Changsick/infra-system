package com.assign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.assign.jwt.filter.JwtAuthenticationFilter;
import com.assign.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
//@EnableMethodSecurity
//@EnableWebFluxSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	private final SecurityProperties securityProperties;
	
//	private final UserDetailsService userDetailService;
	
//	private final UserService service;
	
//	private final JwtReactiveAuthenticationWebFilter jwtReactiveAuthenticationWebFilter;
	
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
	        .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT 사용 시 필수)
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
	        .authorizeHttpRequests(auth -> auth
	        	.requestMatchers(securityProperties.getUrls().toArray(new String[0])).permitAll()
	            .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
	            .anyRequest().authenticated() // 나머지는 인증 필요
	        )
	        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	// 인증 매니저 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
//	@Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userDetailService);
//        return authenticationManagerBuilder.build();
//    }
    
	/*
	@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        		.addFilterBefore(jwtReactiveAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        		.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        		.authorizeExchange(exchange -> exchange
        				.pathMatchers("/login", "/users/signup",
        	            		"/users/validator",
        	            		"/users/signin",
        	            		"/users/refresh",
        	            		"/v3/api-docs/**",
        	                    "/swagger-ui/**",
        	            		"/swagger-ui.html").permitAll()
                        .pathMatchers("/admin/**", "/reactive/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
        		.anonymous(an -> an.disable())
        		.logout(out -> out.disable())
                .build();
	}
	*/
}
