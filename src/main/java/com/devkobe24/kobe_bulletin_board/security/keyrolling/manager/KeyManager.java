package com.devkobe24.kobe_bulletin_board.security.keyrolling.manager;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class KeyManager {

	private static final String SECRET_KEY = "SECRET_KEY";
	private static final String PREVIOUS_SECRET_KEY = "PREVIOUS_SECRET_KEY";
	private static final String REFRESH_SECRET_KEY = "REFRESH_SECRET_KEY";
	private static final String PREVIOUS_REFRESH_SECRET_KEY = "PREVIOUS_REFRESH_SECRET_KEY";

	private String secretKey;
	private String previousSecretKey;
	private String refreshSecretKey;
	private String previousRefreshSecretKey;

	public KeyManager() {
		this.secretKey = System.getProperty(SECRET_KEY, "default_secret_key");
		this.previousSecretKey = System.getProperty(PREVIOUS_SECRET_KEY, "default_previous_secret_key");
		this.refreshSecretKey = System.getProperty(REFRESH_SECRET_KEY, "default_refresh_secret_key");
		this.previousRefreshSecretKey = System.getProperty(PREVIOUS_REFRESH_SECRET_KEY, "default_previous_secret_key");

		if (this.secretKey == null || this.secretKey.isEmpty()) {
			throw new IllegalStateException("ACTIVE_SECRET_KEY is not set.");
		}

		if (this.refreshSecretKey == null || this.refreshSecretKey.isEmpty()) {
			// 로그만 출력하고 예외는 던지지 않음
			System.out.println("REFRESH_SECRET_KEY is not set.");
		}
	}
}