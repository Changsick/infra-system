package com.assign.jwt;

import static com.assign.constant.UserConstant.JWT_PREFIX;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.assign.dto.TokenInfoDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey key;
	
	private final StringRedisTemplate redisTemplate;
	
	private final long validityInMilliseconds = 1000 * 60 * 60; // 1시간
	
	private final long refreshExpirationMilliseconds = 1000 * 60 * 120; // 2시간
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey
			, StringRedisTemplate redisTemplate
			) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		this.redisTemplate = redisTemplate;
	}
	
	public TokenInfoDTO createToken(String username, List<String> roles) {
	    List<GrantedAuthority> authorities = roles.stream()
	            .map(SimpleGrantedAuthority::new)
	            .collect(Collectors.toList());
		
		ClaimsBuilder claimsBuilder = Jwts.claims()
	            .subject(username)
	            .add("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        String jwtToken = Jwts.builder()
                .claims(claimsBuilder.build())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        
        return TokenInfoDTO.builder()
        		.token(JWT_PREFIX + jwtToken)
        		.iat(now.getTime())
        		.exp(validity.getTime())
        		.build();
    }
	
	public String createRefreshToken(String username, String refreshId) {
		
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshExpirationMilliseconds);
		String jwtToken = Jwts.builder()
				.subject(username)
				.id(refreshId)
				.issuedAt(now)
				.expiration(validity)
				.signWith(key, Jwts.SIG.HS256)
				.compact();
		
		return jwtToken;
	}

	// jwt 만료에 상관없이 email을 꺼내오기 위함
    public String getUsername(String token) {
    	
    	try {			
    		return parseClaims(token).getSubject();
		} catch (ExpiredJwtException e) {
			return e.getClaims().getSubject();
		} catch (Exception e) {
			return null;
		}
    }
    
    public String getRefreshId(String token) {
    	
    	try {			
    		return parseClaims(token).getId();
    	} catch (ExpiredJwtException e) {
    		return e.getClaims().getId();
    	} catch (Exception e) {
    		return null;
    	}
    }
    
    public String resolveToken(String token) {
        return token != null ? token.replaceAll(JWT_PREFIX, "") : null;
    }
    
    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | ExpiredJwtException e) {
            // 유효하지 않거나 만료된 토큰, 형식이 잘못된 토큰
            return false;
        }
    }
    
    // JWT 토큰에서 Claims 추출
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("roles"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    
    // 토큰을 블랙리스트에 추가 (로그아웃 시)
    public void blacklistToken(String token) {
        String redisKey = "logout:" + token;
        redisTemplate.opsForValue().set(redisKey, "true", validityInMilliseconds, TimeUnit.MILLISECONDS);  // 유효 기간 동안 블랙리스트에 저장
    }

    // 블랙리스트 여부 체크
    public boolean isBlacklisted(String token) {
        String redisKey = "logout:" + token;
        return redisTemplate.hasKey(redisKey);
    }
    
    public void saveRefreshToken(String token) {
    	String refreshKey = "refreshToken:" + token;
    	redisTemplate.opsForValue().set(refreshKey, "true", refreshExpirationMilliseconds, TimeUnit.MILLISECONDS);
    }
    
    public boolean isValidRefreshToken(String token) {
    	String refreshKey = "refreshToken:" + token;
        return redisTemplate.hasKey(refreshKey);
    }
    
    public void deleteRefreshToken(String token) {
    	String refreshKey = "refreshToken:" + token;
    	redisTemplate.delete(refreshKey);
    }
    
}
