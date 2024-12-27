package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.PostCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostCredentialRepository extends JpaRepository<PostCredentials, Long> {

	@Query("SELECT p " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false")
	Optional<PostCredentials> findActivePostCredentialsByPostId(@Param("postId") Long postId);

	@Query("SELECT p.postToken " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.hashedPassword = :hashedPassword " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false ")
	Optional<PostCredentials> findValidTokenByPostIdWithHashedPassword(@Param("postId") Long postId, @Param("hashedPassword") String hashedPassword);

	@Query("SELECT p.postToken " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.hashedPassword = :hashedPassword " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false ")
	Optional<String> findValidPostToken(@Param("postId") Long postId, @Param("hashedPassword") String hashedPassword);

	@Query("SELECT p.postToken " +
		"FROM PostCredentials p " +
		"WHERE p.post.id = :postId " +
		"AND p.isRevoked = false " +
		"AND p.isExpired = false")
	Optional<PostCredentials> findValidPostTokenByPostId(@Param("postId") Long postId);
}
