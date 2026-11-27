package com.promantia.openbravonetsuiteintegration.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final JdbcTemplate jdbcTemplate;

	public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		String sql = """
				SELECT username, password, active
				FROM users
				WHERE username = ?
				""";

		return jdbcTemplate.query(sql, rs -> {

			if (!rs.next()) {
				throw new UsernameNotFoundException("User not found");
			}

			return User.withUsername(rs.getString("username")).password(rs.getString("password"))
					.disabled(!rs.getBoolean("active")).roles("USER").build();

		}, username);
	}
}