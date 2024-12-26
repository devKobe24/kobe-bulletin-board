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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteService {

	private final PostRepository postRepository;
	private final CommonService<Post, Long> commonService;
	private final Hasher hasher;
	private final PostCredentialRepository postCredentialRepository;

	@Transactional(transactionManager = "deletePostTransactionManager")
	public DeletePostResponse deletePost(DeletePostRequest request, String authorizationHeader) {
		// Post token 가져오기
		String token = request.getPostToken();
		// UserRole 추출
		String userRole = JWTProvider.getUserRoleFromToken(token);
		// 게시물 조회 및 게시 유무 여부 확인.
		Post existingPost = existsPost(request);

		if (userRole.equals(UserRole.ADMIN.getValue())) {
			// 유저의 역할이 ADMIN 일 때 삭제 로직.
			deletePostWhenAdmin(existingPost);
		} else {
			// 유저의 역할이 USER(일반 유저)일 때 삭제 로직.
			deletePostWhenUser(request, existingPost, authorizationHeader);
		}
		return new DeletePostResponse(ResponseCode.SUCCESS);
	}

	// 유저의 역할이 ADMIN 일 때 삭제 로직.
	private void deletePostWhenAdmin(Post existingPost) {
		log.debug("Admin is deleting the post with ID: {}", existingPost.getId());
		deletePostAndInvalidateToken(Optional.empty(), existingPost);
	}

	// 유저의 역할이 USER(일반 유저)일 때 삭제 로직.
	private void deletePostWhenUser(DeletePostRequest request, Post existingPost, String authorizationHeader) {
		Long postId = request.getId();
		// 게시물 비밀번호 검증 후 추출한 비밀 번호
		validatePostPassword(request, existingPost);
		// 토큰 검증, 추츨, 비교
		String token = validatePostToken(authorizationHeader);
		// 게시물 삭제
		deletePostAndInvalidateToken(Optional.ofNullable(token), existingPost);
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

	private String validatePostToken(String authorizationHeader) {
		try {
			String token = JWTProvider.extractBearerToken(authorizationHeader);
			return token;
		} catch (CustomException e) {
			log.warn(e.getMessage());
			throw new CustomException(ResponseCode.TOKEN_IS_NOT_EXISTS);
		}
	}

	// 게시물 삭제 및 토큰 revoke, expired
	private void deletePostAndInvalidateToken(Optional<String> optionalToken, Post existingPost) {
		// Post Token 언래핑
		String token = unWrappedToken(optionalToken);
		// Post ID 추출(String Type)
		String extractedId = JWTProvider.getPostIdFromToken(token);
		// Post ID Long 타입으로 변환
		Long postId = convertedPostId(extractedId);
		// Post token revoke, expired(Post Token 무효화)
		invalidatedPostToken(postId);
		// 게시물 삭제
		postRepository.delete(existingPost);
	}

	// Unwrapping 메서드
	private String unWrappedToken(Optional<String> optionalToken) {
		// Unwrapping
		String token = optionalToken.orElseThrow(() -> {
			log.warn("Token not found");
			return new CustomException(ResponseCode.TOKEN_IS_NOT_EXISTS);
		});
		return token;
	}

	// Post Id Long 타입으로 변환 메서드
	private Long convertedPostId(String extractedId) {
		try {
			Long convertedPostId = Long.valueOf(extractedId);
			return convertedPostId;
		} catch (NumberFormatException e) {
			log.warn("숫자 형식 오류 message: {}", e.getMessage());
			throw new NumberFormatException(e.getMessage());
		}
	}

	// Post token revoke, expired 메서드
	// Post token 무효화 메서드
	private void invalidatedPostToken(Long postId) {
		Optional<PostCredentials> validPostToken = postCredentialRepository.findValidPostTokenByPostId(postId);

		validPostToken.ifPresent(postToken -> {
			// 토큰 revoke
			postToken.setRevoked(true);
			// 토큰 expired
			postToken.setExpired(true);
		});
	}
}
