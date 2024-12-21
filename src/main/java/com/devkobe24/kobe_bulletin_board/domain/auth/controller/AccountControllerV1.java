package com.devkobe24.kobe_bulletin_board.domain.auth.controller;

import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.UpdatePasswordRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.UpdatePasswordResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.service.PasswordUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account API", description = "V1 Account API")
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountControllerV1 {

	private final PasswordUpdateService passwordUpdateService;

	@Operation(
		summary = "비밀번호 변경",
		description = "사용자의 비밀번호를 변경합니다."
	)
	@PatchMapping("/update/password")
	public UpdatePasswordResponse updatePassword(
		@RequestBody @Valid UpdatePasswordRequest request
	) {
		return passwordUpdateService.updatePassword(request);
	}
}
