package com.devkobe24.kobe_bulletin_board.config;

import com.devkobe24.kobe_bulletin_board.common.sevice.CommonService;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostServiceConfig {
	@Bean
	public CommonService<Post, Long> postCommonService(PostRepository postRepository) {
		return new CommonService<>(postRepository);
	}
}
