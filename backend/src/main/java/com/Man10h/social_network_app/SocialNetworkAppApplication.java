package com.Man10h.social_network_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SocialNetworkAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkAppApplication.class, args);
	}
}

