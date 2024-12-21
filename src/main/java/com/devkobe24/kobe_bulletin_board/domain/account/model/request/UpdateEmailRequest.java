package com.devkobe24.kobe_bulletin_board.domain.account.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이메일 수정 Request")
public class UpdateEmailRequest {

	@Schema(description = "수정하고자 하는 유저의 ID", example = "1")
	@NotNull
	private Long id;

	@Schema(description = "수정 전 이메일", example = "previous@example.com")
	@NotNull
	private String previousEmail;

	@Schema(description = "수정 후 이메일", example = "new@example.com")
	@NotNull
	private String newEmail;
}
