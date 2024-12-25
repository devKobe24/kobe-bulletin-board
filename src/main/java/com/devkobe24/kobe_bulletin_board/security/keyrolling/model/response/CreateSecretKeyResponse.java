package com.devkobe24.kobe_bulletin_board.security.keyrolling.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "SECRET KEY 생성 Response")
@Getter
public class CreateSecretKeyResponse {

	@Schema(description = "응답 코드")
	private Integer code;

	@Schema(description = "응답 메시지")
	private String message;

	public CreateSecretKeyResponse(ResponseCode responseCode) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}
}
