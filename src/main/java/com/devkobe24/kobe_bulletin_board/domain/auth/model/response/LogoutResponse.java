package com.devkobe24.kobe_bulletin_board.domain.auth.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그아웃 Response")
public class LogoutResponse {
	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	public LogoutResponse(ResponseCode responseCode) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}
}
