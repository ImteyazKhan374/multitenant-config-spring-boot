package com.tcs.unison.security;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIContext {

	private static final ThreadLocal<APIContext> userContext = new ThreadLocal<>();

	private String username;
	private String token;
	private List<String> roles;
	private String tenantId;

	public static void set(APIContext context) {
		userContext.set(context);
	}

	public static APIContext get() {
		return userContext.get();
	}

	public static void clear() {
		userContext.remove();
	}
}
