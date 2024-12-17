package com.devkobe24.kobe_bulletin_board.domain.post.service;

import com.devkobe24.kobe_bulletin_board.domain.repository.PostUpdateRepository;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateService {

	private final PostUpdateRepository postUpdateRepository;
	private final Hasher hasher;


}
