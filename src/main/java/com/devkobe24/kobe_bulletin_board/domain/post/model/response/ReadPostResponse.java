package com.devkobe24.kobe_bulletin_board.domain.post.model.response;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Schema(description = "게시물 읽기 Response")
@Getter
public class ReadPostResponse {

	@Schema(description = "응답 코드")
	private final Integer code;

	@Schema(description = "응답 메시지")
	private final String message;

	@Schema(description = "게시물 번호")
	private final Long id;

	@Schema(description = "게시물 제목")
	private final String title;

	@Schema(description = "게시물 본문")
	private final String content;

	@Schema(description = "글쓴이")
	private final String nickName;

	@Schema(description = "게시물 작성일")
	private final Date createdAt;

	@Schema(description = "게시물 조회수")
	private final Integer viewCount;

	public ReadPostResponse(ResponseCode responseCode, Post post) {
		this.code = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.id = post.getId();
		this.title = post.getTitle();
		this.content = post.getContent();
		this.nickName = post.getUser().getNickName();
		this.createdAt = post.getCreatedAt();
		this.viewCount = post.getViewCount();
	}
}