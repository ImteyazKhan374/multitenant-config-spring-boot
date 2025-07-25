package com.abkatk.unison.service;

import com.abkatk.unison.model.User;

public interface UserService {
	
	User createUser(User user);
	
	User findById(Integer id);
}