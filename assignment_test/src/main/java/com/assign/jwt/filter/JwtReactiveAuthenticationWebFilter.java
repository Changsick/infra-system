package com.assign.jwt.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.assign.jwt.JwtTokenProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
//@ConditionalOnClass(name = "org.springframework.web.server.WebFilter") // WebFlux 환경에서만 등록
public class JwtReactiveAuthenticationWebFilter implements WebFilter {

	private final JwtTokenProvider jwtTokenProvider;

    public JwtReactiveAuthenticationWebFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		log.info("##################### reactive filter");
		ServerHttpRequest request = exchange.getRequest();
        String token = jwtTokenProvider.resolveToken(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        if (token != null && jwtTokenProvider.validateToken(token)) {
            if (jwtTokenProvider.isBlacklisted(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        return chain.filter(exchange);
	}

}
