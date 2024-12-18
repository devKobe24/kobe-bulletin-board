package com.devkobe24.kobe_bulletin_board.common.sevice;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.repository.PostRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommonService {

	private final PostRepository postRepository;

	public Post findPostById(Long id) {
		return postRepository.findById(id)
			.orElseThrow(() -> {
				log.error("Post not found with id {}", id);
				return new CustomException(ResponseCode.POST_NOT_FOUND);
			});
	}
}
