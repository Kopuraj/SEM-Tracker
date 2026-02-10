package com.kops.sem_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
// import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@SpringBootApplication(exclude = {MailSenderAutoConfiguration.class})
// @EnableScheduling - Disabled to remove email scheduling feature
public class SemTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemTrackerApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * CORS Configuration moved to SecurityConfig.java to avoid duplicate bean definition error.
	 * The application failed to start because both classes defined 'corsConfigurationSource'.
	 */
}