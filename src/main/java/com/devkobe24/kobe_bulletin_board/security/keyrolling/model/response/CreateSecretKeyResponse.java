package com.devkobe24.kobe_bulletin_board.security.keyrolling.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "SECRET KEY 생성 Response")
@Getter
public class CreateSecretKeyResponse {

	@Schema(description = "생성된 SECRET KEY")
	private final String secretKey;

	public CreateSecretKeyResponse(String secretKey) {
		this.secretKey = secretKey;
	}
}
