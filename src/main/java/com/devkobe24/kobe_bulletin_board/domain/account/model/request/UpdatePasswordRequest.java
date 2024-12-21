package com.devkobe24.kobe_bulletin_board.domain.account.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "비밀번호 변경 Request")
public class UpdatePasswordRequest {
	@Schema(description = "유저 ID", example = "1")
	private Long id;

	@Schema(description = "변경 전 유저 비밀번호")
	private String previousPassword;

	@Schema(description = "변경 할 유저 비밀번호")
	private String newPassword;
}
