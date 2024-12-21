package com.devkobe24.kobe_bulletin_board.domain.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그아웃 Request")
public record LogoutRequest(

	@Schema(description = "Revoke할 사용자 인증 토큰", example = "Bearer <access_token>")
	@NotBlank
	String token
) {}
