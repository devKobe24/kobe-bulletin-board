package com.devkobe24.kobe_bulletin_board.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig {
	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customTomcatWebServer() {
		return factory -> {
			Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
			connector.setScheme("http");
			connector.setPort(8080); // HTTP 포트
			connector.setSecure(false);
			connector.setRedirectPort(443); // HTTPS 포트로 리다이렉트
			factory.addAdditionalTomcatConnectors(connector);
		};
	}
}
