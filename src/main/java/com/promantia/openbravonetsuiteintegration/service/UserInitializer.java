package com.promantia.openbravonetsuiteintegration.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.promantia.openbravonetsuiteintegration.config.UserConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserInitializer implements CommandLineRunner {

	private final JdbcTemplate jdbcTemplate;
	private final PasswordEncoder passwordEncoder;
	private final UserConfig userProperties;

	public UserInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, UserConfig userProperties) {

		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
		this.userProperties = userProperties;
	}

	@Override
	@Transactional
	public void run(String... args) {

		for (UserConfig.AppUser user : userProperties.getUsers()) {

			String username = user.getUsername();
			String rawPassword = user.getPassword();

			if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
				throw new IllegalArgumentException("Username and password cannot be empty");
			}

			Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class,
					username);

			if (count != null && count == 0) {

				String encryptedPassword = passwordEncoder.encode(rawPassword);

				jdbcTemplate.update("INSERT INTO users (username, password, active) " + "VALUES (?, ?, TRUE)", username,
						encryptedPassword);

				log.info("User inserted successfully: " + username);

			} else {

				log.info("User already exists, skipped: " + username);
			}
		}
	}
}