package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.CreatePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.CreatePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostCredentialRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.PostCredentials;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
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

	private final PostRepository postRepository;
	private final Hasher hasher;
	private final UserRepository userRepository;
	private final PostCredentialRepository postCredentialRepository;

	@Transactional(transactionManager = "createPostTransactionManager")
	public CreatePostResponse createPost(CreatePostRequest request, String nickName) {

		User writer = userRepository.findByNickName(nickName)
			.orElseThrow(() -> {
				log.error("User not found with nickname: {}", nickName);
				return new CustomException(ResponseCode.USER_NOT_EXISTS);
			});

		Post newPost = this.newPost(request.title(), request.content(), request.password(), writer);

		Post savedPost = postRepository.save(newPost);
		validateSavedPost(savedPost);

		String hashedPassword = hasher.getHashingValue(request.password());
		String token = postCredentialRepository.findValidToken(newPost.getId(), hashedPassword).orElseThrow(() -> {
			log.error("Could not find valid token for post: {}", newPost.getId());
			throw new CustomException(ResponseCode.TOKEN_IS_INVALID);
		});

		return new CreatePostResponse(ResponseCode.SUCCESS, token);
	}

	private Post newPost(String title, String content, String password, User writer) {
		String hashedPassword = hasher.getHashingValue(password);
		try {
			Post newPost = Post.builder()
				.title(title)
				.content(content)
				.password(hashedPassword)
				.user(writer)
				.createdAt(new Timestamp(System.currentTimeMillis()))
				.viewCount(0)
				.build();

			createPostCredentials(newPost, hashedPassword);

			return newPost;
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_NOT_EXISTS);
		}
	}

	private PostCredentials createPostCredentials(Post newPost, String hashedPassword) {
		String newPostNickName = newPost.getUser().getNickName();
		String newPostPassword = newPost.getPassword();

		String newPostToken = JWTProvider.createPostToken(newPostNickName, newPostPassword);

		PostCredentials postCredentials = PostCredentials.builder()
			.post(newPost)
			.hashedPassword(hashedPassword)
			.token(newPostToken)
			.isRevoked(false)
			.isExpired(false)
			.build();

		postCredentialRepository.save(postCredentials);

		return postCredentials;
	}

	private void validateSavedPost(Post savedPost) {
		if (savedPost == null) {
			log.error("POST_SAVED_FAILED: Post is null");
			throw new CustomException(ResponseCode.POST_SAVED_FAILED);
		}
	}
}
