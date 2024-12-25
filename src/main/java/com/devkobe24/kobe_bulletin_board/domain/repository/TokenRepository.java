package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	// 입력된 token 값만을 기준으로 조회
	// 조건: token 값만 일치하면 가져옵니다.
	Optional<Token> findByToken(String token);

	// 입력퇸 token 값이 일치하면서 isRevoked = false 와 isExpired = false 인 경우만 조회
	// 조건: 토큰이 유효해야 합니다.
	@Query("SELECT t " +
		"FROM token t " +
		"WHERE t.token = :token " +
		"AND t.isRevoked = false " +
		"AND t.isExpired = false")
	Optional<Token> findValidToken(@Param("token") String token);

	// 토큰이 revoked 안된것만 존재여부
	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
		"FROM token t " +
		"WHERE t.user.id = :userId AND t.isRevoked = false")
	boolean existsByUserIdAndIsRevokedFalse(@Param("userId") Long userId);

	// 토큰 조회 메서드
	@Query("SELECT t " +
		"FROM token t " +
		"WHERE t.user.id = :userId " +
		"AND t.isRevoked = false")
	Optional<Token> findByUserIdAndIsRevokedFalse(@Param("userId") Long userId);
}
