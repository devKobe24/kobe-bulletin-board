package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostUpdateRepository extends JpaRepository<Post, Long> {
	Optional<Post> findById(Long id);
}
