package com.devkobe24.kobe_bulletin_board.security.keyrolling.controller;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.security.keyrolling.model.response.CreateRefreshSecretKeyResponse;
import com.devkobe24.kobe_bulletin_board.security.keyrolling.model.response.CreateSecretKeyResponse;
import com.devkobe24.kobe_bulletin_board.security.keyrolling.service.KeyRotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class KeyRotationControllerV1 {

	private final KeyRotationService keyRotationService;

	@PostMapping("/rotate-keys")
	public CreateSecretKeyResponse rotateKeys() {
		String activeKey = keyRotationService.rotateSecretKey();

		return new CreateSecretKeyResponse(ResponseCode.SUCCESS);
	}

	@PostMapping("/rotate-refreshKey")
	public CreateRefreshSecretKeyResponse rotateRefreshKey() {
		String activeKey = keyRotationService.rotateRefreshSecretKey();

		return new CreateRefreshSecretKeyResponse(ResponseCode.SUCCESS);
	}
}
