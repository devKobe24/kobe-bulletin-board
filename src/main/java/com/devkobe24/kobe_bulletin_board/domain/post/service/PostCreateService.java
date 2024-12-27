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
	public CreatePostResponse createPost(CreatePostRequest request, String authorizationHeader) {
		User user = user(authorizationHeader);
		Post newPost = this.newPost(request, user);
		Post savedPost = postRepository.save(newPost);

		validateSavedPost(savedPost);

		PostCredentials postCredentials = postCredentials(newPost);

		return new CreatePostResponse(ResponseCode.SUCCESS, postCredentials);
	}

	private PostCredentials postCredentials(Post newPost) {
		Long postId = newPost.getId();
		PostCredentials postCredentials = postCredentialRepository.findActivePostCredentialsByPostId(postId)
			.orElseThrow(() -> {
			log.warn("Couldn't find active post credentials for {}", postId);
			throw new CustomException(ResponseCode.POST_CREDENTIALS_NOT_FOUND);
		});
		return postCredentials;
	}

	private User user(String authorizationHeader) {
		// Access Token 추출
		String extractedToken = JWTProvider.extractBearerToken(authorizationHeader);
		// Email 추출
		String extractedEmail = JWTProvider.getEmailFromToken(extractedToken);

		// User
		User user = userRepository.findByEmail(extractedEmail).orElseThrow(() -> {
			log.warn("User not found with email {}", extractedEmail);
			throw new CustomException(ResponseCode.USER_NOT_EXISTS);
		});

		return user;
	}

	private Post newPost(CreatePostRequest request, User user) {
		String hashedPassword = hasher.getHashingValue(request.password());
		try {
			Post newPost = Post.builder()
				.title(request.title())
				.content(request.content())
				.password(hashedPassword)
				.user(user)
				.createdAt(new Timestamp(System.currentTimeMillis()))
				.viewCount(0)
				.build();
			postRepository.save(newPost);

			// TODO: savePostCredentials
			savePostCredentials(newPost, hashedPassword);

			return newPost;
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_NOT_EXISTS);
		}
	}

	// Save PostCredentials
	private void savePostCredentials(Post newPost, String hashedPassword) {

		User user = newUser(newPost);
		UserRole userRole = newUserRole(user);
		String newPostToken = newPostToken(newPost, user, userRole);

		PostCredentials postCredentials = PostCredentials.builder()
			.post(newPost)
			.hashedPassword(hashedPassword)
			.postToken(newPostToken)
			.isRevoked(false)
			.isExpired(false)
			.build();

		postCredentialRepository.save(postCredentials);
	}

	private void validateSavedPost(Post savedPost) {
		if (savedPost == null) {
			log.error("POST_SAVED_FAILED: Post is null");
			throw new CustomException(ResponseCode.POST_SAVED_FAILED);
		}
	}
}
