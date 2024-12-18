package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.ReadPostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.ReadPostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostReadRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReadService {

	private final PostRepository postRepository;

	@Transactional(transactionManager = "readPostTransactionManager")
	public ReadPostResponse readPost(ReadPostRequest request) {
		try {
			Post post = postRepository.findPostByIdWithLock(request.getId())
				.orElseThrow(() -> {
					log.error("Post not found with id: {}", request.getId());
					return new CustomException(ResponseCode.POST_NOT_EXISTS);
				});

			// 조회수 증가.
			post.incrementViewCount();

			// 변경사항 저장.
			postRepository.save(post);

			// 응답 생성
			return new ReadPostResponse(ResponseCode.SUCCESS, post);
		} catch (Exception e) {
			log.error("Unexpected error while reading post: {}", e.getMessage());
			throw new CustomException(ResponseCode.POST_READ_FAILED, e.getMessage());
		}
	}
}
