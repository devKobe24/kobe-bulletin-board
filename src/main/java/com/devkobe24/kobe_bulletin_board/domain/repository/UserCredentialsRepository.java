package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
	@Query("SELECT u.role " +
		"FROM UserCredentials u " +
		"WHERE u.user.id = :userId " +
		"AND u.hashedPassword = :hashedPassword")
	Optional<String> getRole(@Param("userId") Long userId, @Param("hashedPassword") String hashedPassword);
}
