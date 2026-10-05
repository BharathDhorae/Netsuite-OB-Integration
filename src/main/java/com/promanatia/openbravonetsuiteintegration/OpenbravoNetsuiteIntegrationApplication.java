package com.promanatia.openbravonetsuiteintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OpenbravoNetsuiteIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenbravoNetsuiteIntegrationApplication.class, args);
	}

}
