package com.assign.jwt.filter;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.assign.jwt.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
//@ConditionalOnClass(name = "javax.servlet.Filter") // Servlet 환경에서만 등록
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("@@@@@@@@@@@@@@@ servlet filter");
     // 헤더에서 JWT 추출
        String token = jwtTokenProvider.resolveToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하고, 블랙리스트에 있지 않다면 인증 정보를 SecurityContext에 설정
            if (jwtTokenProvider.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                return;
            }
            
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 요청 처리 계속 진행
        filterChain.doFilter(request, response);
	}

}
