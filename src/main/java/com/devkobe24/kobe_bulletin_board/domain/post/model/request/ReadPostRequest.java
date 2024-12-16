package com.devkobe24.kobe_bulletin_board.domain.post.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "게시물 읽기 Request")
public class ReadPostRequest {

	@Schema(description = "읽고자 하는 게시물의 ID", example = "1")
	@NotNull
	private Long id;
}
