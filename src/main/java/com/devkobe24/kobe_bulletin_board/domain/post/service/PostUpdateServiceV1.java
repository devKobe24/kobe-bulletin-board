package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.sevice.CommonService;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.UpdatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.UpdatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateServiceV1 {

	private final PostRepository postRepository;
	private final Hasher hasher;
	private final UserRepository userRepository;
	private final CommonService<Post, Long> postCommonService;

	@Transactional(transactionManager = "updatePostTransactionManager")
	public UpdatePostResponse updatePost(UpdatePostRequest request, String email) {
		try {
			// 사용자 조회
			User user = findByEmail(email);
			// 게시물 조회
			Post existingPost = findPostById(request);

			// 비밀 번호 검증 및 수정(비밀번호 수정 요청이 존재할 경우에만 수행)
			if (!existingPost.getPassword().equals(request.getPassword())) {
				passwordValidation(request, existingPost);
			} else {
				log.error("Request modify same password {}", request.getPassword());
				return new UpdatePostResponse(ResponseCode.MODIFY_SAME_PASSWORD);
			}

			// 제목 수정
			if (!existingPost.getTitle().equals(request.getTitle())) {
				existingPost.setTitle(request.getTitle());
			} else {
				log.error("Request modify same title {}", request.getTitle());
				return new UpdatePostResponse(ResponseCode.MODIFY_SAME_TITLE);
			}

			// 본문 수정
			if (!existingPost.getContent().equals(request.getContent())) {
				existingPost.setContent(request.getContent());
			} else {
				log.error("Request modify same content {}", request.getContent());
				return new UpdatePostResponse(ResponseCode.MODIFY_SAME_CONTENT);
			}

			// 작성자 다시 지정
			existingPost.setUser(user);

			// 수정 시간 갱신
			existingPost.setCreatedAt(new Timestamp(System.currentTimeMillis()));

			// 게시물 저장
			postRepository.save(existingPost);

			return new UpdatePostResponse(ResponseCode.SUCCESS);
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_SAVED_FAILED, e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_SAVED_FAILED, e.getMessage());
		}
	}

	// 사용자 조회 메서드
	private User findByEmail(String email) {
		// 사용자 조회
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> {
				log.error("User not found with email {}", email);
				return new CustomException(ResponseCode.USER_NOT_EXISTS);
			});
		return user;
	}

	// 게시물 조회 메서드
	private Post findPostById(UpdatePostRequest request) {
		// 게시물 조회
		return postCommonService.findById(request.getId(), ResponseCode.SUCCESS);
	}

	// 비밀번호 검증 메서드
	private String passwordValidation(UpdatePostRequest request, Post existingPost) {
		// 비밀번호 검증
		String hashedPassword = hasher.getHashingValue(request.getPassword());
		if (!existingPost.getPassword().equals(hashedPassword)) {
			throw new CustomException(ResponseCode.MISS_MATCH_PASSWORD);
		}
		return hashedPassword;
	}
}
