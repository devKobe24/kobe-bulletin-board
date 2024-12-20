package com.devkobe24.kobe_bulletin_board.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "특정 유저 조회 Request")
public record ReadSpecificUserRequest(
	@Schema(description = "특정 유저 id", example = "1")
	@NotBlank
	@NotNull
	Long id
) {}
