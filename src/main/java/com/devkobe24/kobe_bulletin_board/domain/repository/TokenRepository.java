package com.devkobe24.kobe_bulletin_board.domain.repository;

import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
	// 입력된 token 값만을 기준으로 조회
	// 조건: token 값만 일치하면 가져옵니다.
	Optional<Token> findByToken(String token);

	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
		"FROM token t " +
		"WHERE t.token = :token AND t.token IS NOT NULL AND t.token <> ''")
	boolean existsTokenWithValue(@Param("token") String token);

	// 입력퇸 token 값이 일치하면서 isRevoked = false 와 isExpired = false 인 경우만 조회
	// 조건: 토큰이 유효해야 합니다.
	@Query("SELECT t " +
		"FROM token t " +
		"WHERE t.token = :token " +
		"AND t.isRevoked = false " +
		"AND t.isExpired = false")
	Optional<Token> findValidToken(@Param("token") String token);

	// 특정 사용자의 유효한 토큰을 찾을 수 있습니다.
	// 예를 들어, 로그인 시 다른 토큰을 무효화하는 로직에서 유용합니다.
	@Query("SELECT t " +
		"FROM token t " +
		"WHERE t.user.id = :userId " +
		"AND t.isRevoked = false " +
		"AND t.isExpired = false")
	Optional<Token> findValidTokenByUserId(@Param("userId") Long userId);

	// 이전 토큰 무효화 처리
	// 기존 토큰을 무효화할 때 한 번에 모든 토큰을 무효화해야 합니다.
	@Modifying
	@Query("UPDATE token t SET t.isRevoked = true WHERE t.user.id = :userId")
	void revokeAllTokensByUserId(@Param("userId") Long userId);

	// 토큰이 revoked 된것, 안된것 모두 다 존재여부
	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
		"FROM token t " +
		"WHERE t.user.id = :userId")
	boolean existsByUserIdAndIsRevoked(@Param("userId") Long userId);

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

	// 유저 아이디와 유저 역할(USER, ADMIN)을 이용하여 토큰 조회
	@Query("SELECT t " +
		"FROM token t " +
		"WHERE t.user.id = :userId " +
		"AND t.user.userCredentials.role = :userRole " +
		"AND t.isRevoked = false " +
		"AND t.isExpired = false")
	Optional<Token> findTokenByUserIdAndUserRole(@Param("userId") Long userId, @Param("userRole") UserRole userRole);
}
