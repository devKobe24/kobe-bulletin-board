package com.devkobe24.kobe_bulletin_board;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class KobeBulletinBoardApplication {

	public static void main(String[] args) {
		// Dotenv로 환경 변수 로드
		Dotenv dotenv = Dotenv.configure().load();

		// Dotenv 값을 Map으로 변환
		Map<String, Object> properties = new HashMap<>();
		dotenv.entries().forEach(entry -> properties.put(entry.getKey(), entry.getValue()));

		// SpringApplication 실행
		new SpringApplicationBuilder(KobeBulletinBoardApplication.class)
			.properties(properties)
				.run(args);
	}
}
