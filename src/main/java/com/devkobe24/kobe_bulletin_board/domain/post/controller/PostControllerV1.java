package com.devkobe24.kobe_bulletin_board.domain.post.controller;

import com.devkobe24.kobe_bulletin_board.domain.post.model.request.CreatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.DeletePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.ReadPostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.UpdatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.CreatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.DeletePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.ReadPostResponse;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.UpdatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.post.service.PostCreateService;
import com.devkobe24.kobe_bulletin_board.domain.post.service.PostDeleteService;
import com.devkobe24.kobe_bulletin_board.domain.post.service.PostReadService;
import com.devkobe24.kobe_bulletin_board.domain.post.service.PostUpdateServiceV1;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Post API", description = "V1 Post API")
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // 보안 요구사항 적용
public class PostControllerV1 {

	private final PostCreateService postCreateService;
	private final PostReadService postReadService;
	private final PostUpdateServiceV1 postUpdateServiceV1;
	private final PostDeleteService postDeleteService;

	@Operation(
		summary = "새로운 게시물을 생성합니다.",
		description = "새로운 게시물 생성."
	)
	@PostMapping("/create-post")
	public CreatePostResponse createPost(
		@RequestBody @Valid CreatePostRequest request,
		@RequestHeader("Authorization") String authorizationHeader
		) {
		log.debug("Authorization Header: {}", authorizationHeader);
		String token = JWTProvider.extractBearerToken(authorizationHeader);
		log.debug("Extracted Token: {}", token);
		String email = JWTProvider.getEmailFromToken(token);
		log.debug("Email from Token: {}", email);
		return postCreateService.createPost(request, email);
	}

	@Operation(
		summary = "게시물을 읽습니다.",
		description = "작성된 게시물을 읽습니다."
	)
	@GetMapping("/{postId}")
	public ReadPostResponse readPost(
		@PathVariable Long postId
	) {
		// ReadPostRequest 객채 생성
		ReadPostRequest request = new ReadPostRequest();
		request.setId(postId);
		// 위와 같이 코드를 유지한 이유
		// Service 로직에서 readPost(Long postId)로 수정하면 더 쉽게 코드를 활용할 수 있지만
		// 추후에 코드의 확장성을 대비하여 readPost(ReadPostRequest request)를 유지하게 되었습니다.

		ReadPostResponse readPost = postReadService.readPost(request);
		return readPost;
	}

	@Operation(
		summary = "게시물을 수정합니다.",
		description = "생성했던 게시물의 컨텐츠들을 수정."
	)
	@PutMapping("/update-post")
	public UpdatePostResponse updatePost(
		@RequestBody @Valid UpdatePostRequest request,
		@RequestHeader("Authorization") String authorizationHeader
	) {
		String token = JWTProvider.extractBearerToken(authorizationHeader);
		String email = JWTProvider.getEmailFromToken(token);
		return postUpdateServiceV1.updatePost(request, email);
	}

	@Operation(
		summary = "게시물을 삭제합니다.",
		description = "생성했었던 게시물을 삭제합니다."
	)
	@DeleteMapping("/delete-post")
	public DeletePostResponse deletePost(
		@RequestBody @Valid DeletePostRequest request,
		@RequestHeader("Authorization") String authorizationHeader
	) {
		String token = JWTProvider.extractToken(authorizationHeader);

		String userRole = JWTProvider.getUserRoleFromToken(token);

		return postDeleteService.deletePost(request, userRole);
	}
}
