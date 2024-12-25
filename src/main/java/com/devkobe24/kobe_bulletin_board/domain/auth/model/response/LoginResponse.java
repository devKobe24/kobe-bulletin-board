package com.devkobe24.kobe_bulletin_board.domain.auth.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 Response")
@Getter
public class LoginResponse {
	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	@Schema(description = "Access Token")
	private final String accessToken;

	@Schema(description = "Refresh Token")
	private final String refreshToken;

	public LoginResponse(ResponseCode responseCode, String accessToken, String refreshToken) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
}
