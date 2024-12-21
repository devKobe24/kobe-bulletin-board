package com.devkobe24.kobe_bulletin_board.domain.account.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "닉네임 수정 Response")
public class UpdateNickNameResponse {
	@Schema(description = "응답코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	public UpdateNickNameResponse(ResponseCode responseCode) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}
}
