package com.devkobe24.kobe_bulletin_board.domain.post.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시물 수정 Request")
public class UpdatePostRequest {

	@Schema(description = "수정하고자 하는 게시물의 ID", example = "1")
	@NotNull
	private Long id;

	@Schema(description = "수정하고자 하는 게시물의 제목")
	private String title;

	@Schema(description = "수정하고자 하는 게시물의 본문")
	private String content;

	@Schema(description = "수정하고자 하는 게시물의 비밀번호")
	private String password;
}
