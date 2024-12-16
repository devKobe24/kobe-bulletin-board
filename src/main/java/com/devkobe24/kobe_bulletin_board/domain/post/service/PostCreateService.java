package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.CreatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.CreatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostCreateRepository;
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
public class PostCreateService {

	private final PostCreateRepository postCreateRepository;
	private final Hasher hasher;
	private final UserRepository userRepository;

	@Transactional(transactionManager = "createPostTransactionManager")
	public CreatePostResponse createPost(CreatePostRequest request, String nickName) {
		try {
			User writer = userRepository.findByNickName(nickName)
				.orElseThrow(() -> {
					log.error("User not found with nickname: {}", nickName);
					return new CustomException(ResponseCode.USER_NOT_EXISTS);
				});

			Post newPost = this.newPost(request.title(), request.content(), request.password(), writer);

			Post savedPost = postCreateRepository.save(newPost);
			validateSavedPost(savedPost);
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_SAVED_FAILED, e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_SAVED_FAILED, e.getMessage());
		}
		return new CreatePostResponse(ResponseCode.SUCCESS);
	}

	private Post newPost(String title, String content, String password, User writer) {
		String hashedPassword = hasher.getHashingValue(password);

		Post newPost = Post.builder()
			.title(title)
			.content(content)
			.password(hashedPassword)
			.user(writer)
			.createdAt(new Timestamp(System.currentTimeMillis()))
			.viewCount(0)
			.build();
		return newPost;
	}

	private void validateSavedPost(Post savedPost) {
		if (savedPost == null) {
			log.error("POST_SAVED_FAILED: Post is null");
			throw new CustomException(ResponseCode.POST_SAVED_FAILED);
		}
	}
}
