package com.devkobe24.kobe_bulletin_board.domain.account.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "닉네임 변경 Request")
public class UpdateNickNameRequest {
	@Schema(description = "유저 ID", example = "1")
	private Long id;

	@Schema(description = "수정 전 닉네임")
	private String previousNickName;

	@Schema(description = "수정 후 닉네임")
	private String newNickName;
}
