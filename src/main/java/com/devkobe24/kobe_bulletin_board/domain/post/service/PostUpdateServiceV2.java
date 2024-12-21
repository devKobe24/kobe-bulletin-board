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
public class PostUpdateServiceV2 {

	private final PostRepository postRepository;
	private final Hasher hasher;
	private final UserRepository userRepository;
	private final CommonService<Post, Long> commonService;

	@Transactional(transactionManager = "updatePostTransactionManager")
	public UpdatePostResponse updatePost(UpdatePostRequest request, String nickName) {
		try {
			// 사용자 조회
			User writer = findUserByNickName(nickName);
			// 게시물 조회
			Post existingPost = findPostById(request);

			// 수정 로직
			updateIfChanged("password", request.getPassword(), existingPost.getPassword(),
				() -> passwordValidation(request, existingPost));

			updateIfChanged("title", request.getTitle(), existingPost.getTitle(),
				() -> existingPost.setTitle(request.getTitle()));

			updateIfChanged("content", request.getContent(), existingPost.getContent(),
				() -> existingPost.setContent(request.getContent()));

			// 작성자 다시 지정
			existingPost.setUser(writer);

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
	private User findUserByNickName(String nickName) {
		// 사용자 조회
		User writer = userRepository.findByNickName(nickName)
			.orElseThrow(() -> {
				log.error("User not found with nicknameL {}", nickName);
				return new CustomException(ResponseCode.USER_NOT_EXISTS);
			});
		return writer;
	}

	// 게시물 조회 메서드
	private Post findPostById(UpdatePostRequest request) {
		// 게시물 조회
		return commonService.findById(request.getId(), ResponseCode.SUCCESS);
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

	// 람다식
	private UpdatePostResponse updateIfChanged(String changedFor, String newValue, String oldValue, Runnable updateAction) {
		if (newValue != null && !newValue.equals(oldValue)) {
			updateAction.run();
		} else {
			log.error("Request modify same {} {}", changedFor, newValue);
			return new UpdatePostResponse(ResponseCode.valueOf("MODIFY_SAME_" + changedFor.toUpperCase()));
		}
		return new UpdatePostResponse(ResponseCode.SUCCESS);
	}
}
