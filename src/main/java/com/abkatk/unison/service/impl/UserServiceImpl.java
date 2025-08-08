package com.abkatk.unison.service.impl;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.abkatk.unison.exception.ResourceNotFound;
import com.abkatk.unison.model.User;
import com.abkatk.unison.repository.UserRepository;
import com.abkatk.unison.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	

	@Override
	public User createUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		
		if (user.getId() != null && userRepository.existsById(user.getId())) {
			throw new IllegalArgumentException("User with ID " + user.getId() + " already exists");
		}
		
		return userRepository.save(user);
	}

	@Override
    @Cacheable(value = "user", keyGenerator = "customKeyGenerator")
	public User findById(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}
		
		Optional<User> userOptional = userRepository.findById(id);
		if (!userOptional.isPresent()) {
			throw new ResourceNotFound("User not found with ID: " + id);
		}
		
		// Using get() to retrieve the user, as we know it exists due to the previous check
		return userOptional.get();
	}
}