package com.devkobe24.kobe_bulletin_board.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.addServersItem(new Server()
				.url("https://api.kobe-bulletin-board.com")
				.description("Production Server"))
			.info(new Info()
				.title("Kobe Bulletin Board API")
				.version("V1.0.1")
				.description("API documentation for Kobe Bulletin Board"))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
			.components(new io.swagger.v3.oas.models.Components()
				.addSecuritySchemes("bearerAuth",
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.in(SecurityScheme.In.HEADER)
						.name("Authorization")));
	}
}
