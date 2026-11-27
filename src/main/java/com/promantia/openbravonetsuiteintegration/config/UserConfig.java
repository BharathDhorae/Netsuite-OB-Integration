package com.promantia.openbravonetsuiteintegration.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class UserConfig {

	private List<AppUser> users = new ArrayList<>();

	@Getter
	@Setter
	public static class AppUser {

		private String username;
		private String password;
	}
}