package com.devkobe24.kobe_bulletin_board.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로 허용
			.allowedOriginPatterns("http://localhost:*")
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("*")
			.allowCredentials(true) // 자격 증명 허용
			.maxAge(3600);
	}
}
