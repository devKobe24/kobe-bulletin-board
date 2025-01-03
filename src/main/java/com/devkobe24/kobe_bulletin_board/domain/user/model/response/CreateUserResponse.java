package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "유저 생성 Response")
@Getter
public class CreateUserResponse{

	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	@Schema(description = "유저 역할")
	private final String role;

	public CreateUserResponse(ResponseCode responseCode, String role) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.role = role;
	}
}
