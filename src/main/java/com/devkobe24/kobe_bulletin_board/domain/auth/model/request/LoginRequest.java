package com.devkobe24.kobe_bulletin_board.domain.auth.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "로그인 Request")
public record LoginRequest(
	@Schema(description = "유저 이메일")
	@NotBlank
	@NotNull
	String email,

	@Schema(description = "유저 비밀번호")
	@NotBlank
	@NotNull
	String password
) {}
