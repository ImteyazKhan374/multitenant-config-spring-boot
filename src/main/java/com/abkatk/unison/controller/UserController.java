package com.abkatk.unison.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abkatk.unison.model.User;
import com.abkatk.unison.security.APIContext;
import com.abkatk.unison.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public User create(@RequestBody User user) {
		log.info(APIContext.get().getTenantId());
		return userService.createUser(user);
	}
	

	@GetMapping("find/id/{id}")
	public User findById(@PathVariable Integer id) throws InterruptedException {
		//Thread.sleep(6000);
		log.info(APIContext.get().getTenantId());
		return userService.findById(id);
	}
}