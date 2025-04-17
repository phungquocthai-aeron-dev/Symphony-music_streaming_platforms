package com.phungquocthai.symphony.configuration;

import java.time.LocalDate;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.phungquocthai.symphony.constant.Role;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableScheduling
public class ApplicationInitConfig {

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return ArgsAnnotationPointcut -> {
			if (userRepository.findByRole(Role.ADMIN.getValue()).isEmpty()) {
				User user = User.builder()
							.fullName("Phùng Quốc Thái")
							.avatar("/images/avatars/admin.jpg")
							.phone("0369226360")
							.birthday(LocalDate.of(2004, 3, 3))
							.password(passwordEncoder().encode("admin"))
							.gender(1)
							.role(Role.ADMIN.getValue())
							.build();
				userRepository.save(user);
				log.warn("admin user has been created with default password: 'admin'. Please change it");
			}
		};
	}
	
    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 10);
    }
}
