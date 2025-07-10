package com.tcs.unison.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.tcs.unison.model.User;
import com.tcs.unison.repository.UserRepository;
import com.tcs.unison.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
    @Cacheable(value = "user", keyGenerator = "customKeyGenerator")
	public User findById(Integer id) {
		return userRepository.findById(id).get();
	}
}