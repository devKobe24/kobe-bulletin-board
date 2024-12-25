package com.devkobe24.kobe_bulletin_board.domain.auth.controller;

import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LoginRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LogoutRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LoginResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LogoutResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "V1 Auth API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

	private final AuthService authService;

	@Operation(
		summary = "로그인 처리",
		description = "로그인을 진행합니다."
	)
	@PostMapping("/login")
	public LoginResponse login(
		@RequestBody @Valid LoginRequest request,
		HttpServletResponse response
	) {
		LoginResponse loginResponse = authService.login(request);

		// RefreshToken을 HttpOnly Cookie로 설정.
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(7 * 24 * 60 * 60) // 7일간 유효
			.build();

		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return loginResponse;
	}

	@Operation(
		summary = "로그아웃 처리",
		description = "사용자를 로그아웃 처리합니다."
	)
	@PostMapping("/logout")
	public LogoutResponse logout(
		@RequestBody @Valid LogoutRequest request,
		@CookieValue(value = "refreshToken", required = false) String refreshToken
	) {
		return authService.logout(request, refreshToken);
	}

	@Operation(
		summary = "토큰 갱신",
		description = "RefreshToken을 사용해 AccessToken을 갱신합니다."
	)
	@PostMapping("/refresh-token")
	public LoginResponse refreshToken(
		@CookieValue("refreshToken") String refreshToken,
		HttpServletResponse response
	) {
		LoginResponse loginResponse = authService.refreshToken(refreshToken);

		// 새 RefreshToken을 HttpOnly Cookie로 설정.
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(7 * 24 * 60 * 60) // 7일간 유효
			.build();

		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return loginResponse;
	}
}
