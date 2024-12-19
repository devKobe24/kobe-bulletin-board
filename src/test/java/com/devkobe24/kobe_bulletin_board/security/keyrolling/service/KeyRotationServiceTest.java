package com.devkobe24.kobe_bulletin_board.security.keyrolling.service;

import com.devkobe24.kobe_bulletin_board.security.keyrolling.manager.KeyManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(properties = {
	"ACTIVE_SECRET_KEY=test_active_key",
	"PREVIOUS_SECRET_KEY=test_previous_key"
})
public class KeyRotationServiceTest {

	@Autowired
	private KeyManager keyManager;

	@Autowired
	private KeyRotationService keyRotationService;

	@Test
	public void testKeyRotation() {
		String originalActiveKey = keyManager.getSecretKey();

		// 키 회전 수행
		keyRotationService.rotateSecretKey();

		// 검증
		assertNotEquals(originalActiveKey, keyManager.getSecretKey());
		assertEquals(originalActiveKey, keyManager.getPreviousSecretKey());
	}
}
