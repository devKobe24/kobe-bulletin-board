package com.devkobe24.kobe_bulletin_board.domain.post.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "게시물 삭제 Response")
public class DeletePostResponse {

	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	public DeletePostResponse(ResponseCode responseCode) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
	}
}
