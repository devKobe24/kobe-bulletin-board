package com.devkobe24.kobe_bulletin_board.security.keyrolling.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeyRotationScheduler {

	private final KeyRotationService keyRotationService;

	public KeyRotationScheduler(KeyRotationService keyRotationService) {
		this.keyRotationService = keyRotationService;
	}

	@Scheduled(fixedRate = 86400000) // 하루마다 실행
	public void performKeyRotation() {
		keyRotationService.rotateSecretKey();
	}
}
