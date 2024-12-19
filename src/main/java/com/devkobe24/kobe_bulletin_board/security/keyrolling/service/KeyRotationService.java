package com.devkobe24.kobe_bulletin_board.security.keyrolling.service;

import com.devkobe24.kobe_bulletin_board.security.keyrolling.manager.KeyManager;
import com.devkobe24.kobe_bulletin_board.security.keyrolling.util.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KeyRotationService {

	private final KeyManager keyManager;

	public KeyRotationService(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public String rotateSecretKey() {
		// 새 secret 키 생성
		String newSecretKey = KeyGenerator.generateKey();

		// 현재 키를 이전 키로 이동
		keyManager.setPreviousSecretKey(keyManager.getSecretKey());

		// 새 키를 활성 키로 설정
		keyManager.setSecretKey(newSecretKey);

		// 로깅
		log.info("Key rotation completed.");
		log.info("Secret key: {}", keyManager.getSecretKey());
		log.info("Previous Secret key: {}", keyManager.getPreviousSecretKey());

		return newSecretKey;
	}

	public String rotateRefreshSecretKey() {
		String newRefreshSecretKey = KeyGenerator.generateKey();

		keyManager.setPreviousSecretKey(keyManager.getRefreshSecretKey());

		keyManager.setRefreshSecretKey(newRefreshSecretKey);

		log.info("Refresh Secret key rotation completed.");
		log.info("Refresh Secret key: {}", keyManager.getRefreshSecretKey());
		log.info("Previous Refresh Secret key: {}", keyManager.getPreviousRefreshSecretKey());

		return newRefreshSecretKey;
	}
}
