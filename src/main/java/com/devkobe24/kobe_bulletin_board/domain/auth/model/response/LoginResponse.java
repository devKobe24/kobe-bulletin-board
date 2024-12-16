package com.devkobe24.kobe_bulletin_board.domain.auth.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 Response")
@Getter
public class LoginResponse {
	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "JWT TOKEN")
	private final String token;

	public LoginResponse(ResponseCode responseCode, String token) {
		this.code = responseCode.getCode();
		this.token = token;
	}
}
