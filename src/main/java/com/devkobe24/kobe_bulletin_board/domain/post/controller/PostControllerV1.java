package com.devkobe24.kobe_bulletin_board.domain.post.controller;

import com.devkobe24.kobe_bulletin_board.domain.post.model.request.CreatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.CreatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.post.service.PostService;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post API", description = "V1 Post API")
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostControllerV1 {

	private final PostService postService;

	@Operation(
		summary = "새로운 게시물을 생성합니다.",
		description = "새로운 게시물 생성."
	)
	@PostMapping("/create-post")
	public CreatePostResponse createPost(
		@RequestBody @Valid CreatePostRequest request,
		@RequestHeader("Authorization") String authorizationHeader
		) {
		String token = JWTProvider.extractToken(authorizationHeader);
		String nickname = JWTProvider.getUserFromToken(token);
		return postService.createPost(request, nickname);
	}
}
