package com.devkobe24.kobe_bulletin_board.config;

import com.devkobe24.kobe_bulletin_board.common.sevice.CommonService;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {
	@Bean
	public CommonService<User, Long> userCommonService(UserRepository userRepository) {
		return new CommonService<>(userRepository);
	}
}
