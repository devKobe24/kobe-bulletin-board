package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCreateRepository extends JpaRepository<Post, Long> {
	Optional<Post> findByTitle(String title);
}
