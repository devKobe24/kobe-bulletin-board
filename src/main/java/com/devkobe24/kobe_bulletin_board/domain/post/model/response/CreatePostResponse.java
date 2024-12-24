package com.devkobe24.kobe_bulletin_board.domain.post.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "게시물 생성 Response")
@Getter
public class CreatePostResponse {

	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	@Schema(description = "게시물 토큰")
	private final String postToken;

	public CreatePostResponse(ResponseCode responseCode, String postToken) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.postToken = postToken;
	}
}
