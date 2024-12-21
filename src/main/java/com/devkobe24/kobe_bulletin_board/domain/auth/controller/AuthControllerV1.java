package com.devkobe24.kobe_bulletin_board.domain.auth.controller;

import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LoginRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LogoutRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.UpdatePasswordRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LoginResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LogoutResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.UpdatePasswordResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.service.AuthService;
import com.devkobe24.kobe_bulletin_board.domain.auth.service.PasswordUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "V1 Auth API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

	private final AuthService authService;
	private final PasswordUpdateService passwordUpdateService;

	@Operation(
		summary = "로그인 처리",
		description = "로그인을 진행합니다."
	)
	@PostMapping("/login")
	public LoginResponse login(
		@RequestBody @Valid LoginRequest request
	) {
		return authService.login(request);
	}

	@Operation(
		summary = "로그아웃 처리",
		description = "사용자를 로그아웃 처리합니다."
	)
	@PostMapping("/logout")
	public LogoutResponse logout(
		@RequestBody @Valid LogoutRequest request
	) {
		return authService.logout(request);
	}

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
