package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.common.sevice.CommonService;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.DeletePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.DeletePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostCredentialRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.PostCredentials;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteService {

	private final PostRepository postRepository;
	private final CommonService<Post, Long> commonService;
	private final Hasher hasher;
	private final PostCredentialRepository postCredentialRepository;

	@Transactional(transactionManager = "deletePostTransactionManager")
	public DeletePostResponse deletePost(DeletePostRequest request) {
		// 게시물 조회 및 게시 유무 여부 확인.
		Post existingPost = existsPost(request);

		String userRole = JWTProvider.getUserRoleFromToken(request.getToken());

		if (userRole == UserRole.ADMIN.getValue()) {
			// 유저의 역할이 ADMIN 일 때 삭제 로직.
			deletePostWhenAdmin(existingPost);
		} else {
			// 유저의 역할이 USER(일반 유저)일 때 삭제 로직.
			deletePostWhenUser(request, existingPost);
		}
		return new DeletePostResponse(ResponseCode.SUCCESS);
	}

	// 유저의 역할이 ADMIN 일 때 삭제 로직.
	private void deletePostWhenAdmin(Post existingPost) {
		// 게시물 삭제 및 토큰 revoke, expired
		postRepository.delete(existingPost);
	}

	// 유저의 역할이 USER(일반 유저)일 때 삭제 로직.
	private void deletePostWhenUser(DeletePostRequest request, Post existingPost) {
		Long postId = request.getId();
		// 게시물 비밀번호 검증 후 추출한 비밀 번호
		validatePostPassword(request, existingPost);
		// 토큰 검증, 추츨, 비교
		PostCredentials token = validateToken(postId, request);
		// 게시물 삭제
		deletePost(token, existingPost);
	}

	private Post existsPost(DeletePostRequest request) {
		// 게시물 조회
		Post existingPost = commonService.findById(request.getId(), ResponseCode.SUCCESS);

		if (existingPost == null) {
			log.error("Post not found with id {}", request.getId());
		}
		return existingPost;
	}

	private void validatePostPassword(DeletePostRequest request, Post existingPost) {
		String inPutPostPassword = hasher.getHashingValue(request.getPassword());
		String postPassword = existingPost.getPassword();

		if (!inPutPostPassword.equals(postPassword)) {
			throw new CustomException(ResponseCode.MISS_MATCH_PASSWORD);
		}
	}

	private PostCredentials validateToken(Long postId, DeletePostRequest request) {
		// 토큰 검증
		PostCredentials token = postCredentialRepository.findValidTokenByPostId(postId).orElseThrow(() -> {
			log.error("Token not found with id {}", postId);
			return new CustomException(ResponseCode.TOKEN_IS_INVALID);
		});
		// 토큰 추출
		String extractedTokenFromRequest = JWTProvider.extractToken(request.getToken());
		String extractedTokenFromDB = JWTProvider.extractToken(token.getToken());
		// 토큰 비교
		if (!extractedTokenFromDB.equals(extractedTokenFromRequest)) {
			log.error("Token and extracted token do not match");
			throw new CustomException(ResponseCode.MISS_MATCH_TOKEN);
		}
		return token;
	}

	// 게시물 삭제 및 토큰 revoke, expired
	private void deletePost(PostCredentials token, Post existingPost) {
		// 토큰 revoke
		token.setRevoked(true);
		// 토큰 expired
		token.setExpired(true);
		// 게시물 삭제
		postRepository.delete(existingPost);
	}
}
