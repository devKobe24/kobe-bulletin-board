package com.devkobe24.kobe_bulletin_board.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	@GetMapping("/health")
	public ResponseEntity<String> healthCheck() {
		// 애플리케이션 상태 확인 로직
		boolean isHealthy = checkApplicationHealth();

		if (isHealthy) {
			return ResponseEntity.ok("Healthy");
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Not healthy");
		}
	}


	private boolean checkApplicationHealth() {
		return true;
	}
}
