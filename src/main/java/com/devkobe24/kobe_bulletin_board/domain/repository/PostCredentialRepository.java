package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.PostCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostCredentialRepository extends JpaRepository<PostCredentials, Long> {

	// 특정 사용자의 유효한 토큰을 찾을 수 있습니다.
	@Query("SELECT p " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false")
	Optional<PostCredentials> findValidByPostId(@Param("postId") Long postId);

	@Query("SELECT p.token " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.hashedPassword = :hashedPassword " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false ")
	Optional<String> findValidToken(@Param("postId") Long postId, @Param("hashedPassword") String hashedPassword);
}
