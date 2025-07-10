package com.tcs.unison.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.tcs.unison.dto.AuthRequest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	private String secret = "mysecretkey";

	public String generateToken(AuthRequest authRequest) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("tenantId", authRequest.getTenantId()); // ✅ custom claim
		claims.put("username", authRequest.getUsername()); // optional, subject will also contain this

		return Jwts.builder().setClaims(claims).setSubject(authRequest.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2400)) // 8 hrs
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public String extractTenantId(String token) {
		return (String) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("tenantId");
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()));
	}
}
