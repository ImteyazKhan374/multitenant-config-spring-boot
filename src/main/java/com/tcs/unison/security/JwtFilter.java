package com.tcs.unison.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tcs.unison.util.JwtUtil;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService service;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			
			String requestId = UUID.randomUUID().toString();

			String token = authHeader.substring(7);
			String username = jwtUtil.extractUsername(token);
			String tenantId = jwtUtil.extractTenantId(token);
			
			MDC.put("requestId", requestId);
			MDC.put("tenantId", tenantId);
			MDC.put("user", username);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = service.loadUserByUsername(username);

				if (jwtUtil.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);

					APIContext context = new APIContext();
					context.setUsername(username);
					context.setToken(token);
					context.setRoles(
							userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
					context.setTenantId(tenantId);
					APIContext.set(context);
				}
			}
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			APIContext.clear(); // ✅ Prevent memory leaks
		}
	}

}