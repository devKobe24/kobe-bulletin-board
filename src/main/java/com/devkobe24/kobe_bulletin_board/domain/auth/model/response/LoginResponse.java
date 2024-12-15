package com.devkobe24.kobe_bulletin_board.domain.auth.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 Response")
@Getter
public class LoginResponse {
	@Schema(description = "응답 코드")
	ResponseCode code;

	@Schema(description = "JWT TOKEN")
	String token;

	public LoginResponse(ResponseCode code, String token) {
		this.code = code;
		this.token = token;
	}
}
