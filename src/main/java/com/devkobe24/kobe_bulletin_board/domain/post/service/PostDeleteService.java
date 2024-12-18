package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.sevice.PostCommonService;
import com.devkobe24.kobe_bulletin_board.domain.post.model.request.DeletePostRequest;
import com.devkobe24.kobe_bulletin_board.domain.post.model.response.DeletePostResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteService {

	private final PostRepository postRepository;
	private final PostCommonService postCommonService;

	@Transactional(transactionManager = "deletePostTransactionManager")
	public DeletePostResponse deletePost(DeletePostRequest request) {
		// 게시물 조회
		Post existingPost = findPostById(request);

		if (existingPost == null) {
			log.error("Post not found with id {}", request.getId());
			return new DeletePostResponse(ResponseCode.POST_READ_FAILED);
		}

		// 게시물 삭제
		postRepository.delete(existingPost);

		return new DeletePostResponse(ResponseCode.SUCCESS);
	}

	// 게시물 조회 메서드
	private Post findPostById(DeletePostRequest request) {
		// 게시물 조회
		return postCommonService.findPostById(request.getId());
	}
}
