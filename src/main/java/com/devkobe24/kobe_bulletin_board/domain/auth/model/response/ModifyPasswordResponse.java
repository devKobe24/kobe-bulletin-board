package com.devkobe24.kobe_bulletin_board.domain.auth.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 변경 Response")
public class ModifyPasswordResponse {
	@Schema(description = "응답코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	public ModifyPasswordResponse(ResponseCode responseCode) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}
}
