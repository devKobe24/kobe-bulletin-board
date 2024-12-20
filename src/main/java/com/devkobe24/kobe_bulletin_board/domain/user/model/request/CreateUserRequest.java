package com.devkobe24.kobe_bulletin_board.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저 생성 Request")
public record CreateUserRequest(

	@Schema(description = "유저 이름")
	@NotBlank
	@NotNull
	String userName,

	@Schema(description = "유저 이메일")
	@NotBlank
	@NotNull
	String email,

	@Schema(description = "유저 비밀번호")
	@NotBlank
	@NotNull
	String password,

	@Schema(description = "유저 닉네임")
	@NotBlank
	@NotNull
	String nickName
) {}
