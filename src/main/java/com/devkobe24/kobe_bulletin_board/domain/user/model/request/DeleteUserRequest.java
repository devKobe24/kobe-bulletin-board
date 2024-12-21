package com.devkobe24.kobe_bulletin_board.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 삭제 Request")
public record DeleteUserRequest(
	@Schema(description = "삭제할 유저 id", example = "1")
	Long id
) {}
