package com.abkatk.unison;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
public class UnisonApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnisonApplication.class, args);
	}
}