package com.devkobe24.kobe_bulletin_board.security.keyrolling.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "REFRESH SECRET KEY 생성 Response")
@Getter
public class CreateRefreshSecretKeyResponse {

	@Schema(description = "생성된 REFRESH SECRET KEY")
	private String refreshSecretKey;

	public CreateRefreshSecretKeyResponse(String refreshSecretKey) {
		this.refreshSecretKey = refreshSecretKey;
	}
}
