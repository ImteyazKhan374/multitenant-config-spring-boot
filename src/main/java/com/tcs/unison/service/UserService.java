package com.tcs.unison.service;

import com.tcs.unison.model.User;

public interface UserService {
	
	User createUser(User user);
	
	User findById(Integer id);
}