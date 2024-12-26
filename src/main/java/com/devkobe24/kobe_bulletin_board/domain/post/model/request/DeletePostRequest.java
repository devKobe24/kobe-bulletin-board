package com.devkobe24.kobe_bulletin_board.domain.post.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시물 삭제 Request")
public class DeletePostRequest {

	@Schema(description = "삭제할 게시물 id")
	@NotBlank
	@NotNull
	Long id;

	@Schema(description = "삭제할 게시물 비밀번호")
	String password;

	@Schema(description = "삭제할 게시물 토큰")
	String postToken;
}
