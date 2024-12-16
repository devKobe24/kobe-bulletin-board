package com.devkobe24.kobe_bulletin_board.domain.post.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시물 생성 Request")
public record CreatePostRequest(

	@Schema(description = "제목")
	@NotBlank
	@NotNull
	String title,

	@Schema(description = "본문")
	@NotBlank
	@NotNull
	String content,

	@Schema(description = "비밀번호")
	@NotBlank
	@NotNull
	String password
) {
}
