package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReadRepository extends JpaRepository<Post, Long> {
	Optional<Post> findPostById(Long id);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query("SELECT p FROM Post p WHERE p.id = :id")
	Optional<Post> findPostByIdWithLock(@Param("id") Long id);
}
