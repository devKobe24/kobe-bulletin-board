package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "유저 삭제 Response")
public class DeleteUserResponse {
	@Schema(description = "응답 코드", example = "0")
	Integer code;

	@Schema(description = "응답 메시지", example = "SUCCESS")
	String message;

	public DeleteUserResponse(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
