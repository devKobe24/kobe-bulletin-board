package com.devkobe24.kobe_bulletin_board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.addServersItem(new Server()
				.url("https://api.kobe-bulletin-board.com/")
				.description("Production Server"))
			.info(new Info()
				.title("Kobe Bulletin Board API")
				.version("V1.0.0")
				.description("API Documentation for Kobe Bulletin Board"));
	}
}
