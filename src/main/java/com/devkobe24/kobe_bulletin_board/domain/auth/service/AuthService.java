package com.devkobe24.kobe_bulletin_board.domain.auth.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LoginRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LogoutRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LoginResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LogoutResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.TokenRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Token;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final Hasher hasher;

	@Transactional(transactionManager = "loginTransactionManager")
	public LoginResponse login(LoginRequest request) {
		// 이메일로 사용자 확인
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_EXISTS));

		// 중복 로그인 검사.
		if (tokenRepository.existsByUserIdAndIsRevokedFalse(user.getId())) {
			log.error("USER_ALREADY_LOGIN: {}", request.email());
			throw new CustomException(ResponseCode.USER_ALREADY_LOGIN);
		}

		// 비밀번호 확인
		String hashedValue = hasher.getHashingValue(request.password());
		if (!user.getUserCredentials().getHashedPassword().equals(hashedValue)) {
			throw new CustomException(ResponseCode.MISS_MATCH_PASSWORD);
		}

		// 새로운 엑세스 토큰 및 리프레시 토큰 생성.
		String accessToken = JWTProvider.createAccessToken(user, UserRole.USER);
		String refreshToken = JWTProvider.createRefreshToken(user, UserRole.USER);

		// 토큰 저장
		saveToken(accessToken, user);
		saveToken(refreshToken, user);

		return new LoginResponse(ResponseCode.SUCCESS, accessToken, refreshToken);
	}

	@Transactional(transactionManager = "logoutTransactionManager")
	public LogoutResponse logout(LogoutRequest request, String refreshToken) {
		log.debug("Request token: {}", request.token());

		String extractedToken = JWTProvider.extractToken(request.token());
		log.debug("Extracted token after cleaning: {}", extractedToken);
		log.debug("Extracted token: {}", extractedToken);
		log.debug("Token in database: {}", tokenRepository.findByToken(extractedToken));

		revokeToken(extractedToken);

		if (refreshToken != null) {
			revokeToken(refreshToken);
		}

		return new LogoutResponse(ResponseCode.SUCCESS);
	}

	@Transactional
	public LoginResponse refreshToken(String refreshToken) {
		// 1. RefreshToken 검증
		Token validToken = tokenRepository.findValidToken(refreshToken)
			.orElseThrow(() -> {
				throw new CustomException(ResponseCode.TOKEN_IS_INVALID);
			});

		// 2. 사용자 확인.
		User user = validToken.getUser();

		// 3. 기존 RefreshToken 무효화
		validToken.setRevoked(true);
		tokenRepository.save(validToken);

		// 4. 새로운 AccessToken 생성.
		String newAccessToken = JWTProvider.createAccessToken(user, user.getUserCredentials().getRole());
		String newRefreshToken = JWTProvider.createRefreshToken(user, user.getUserCredentials().getRole());

		// 5. 새 RefreshToken 저장.
		Token newRefreshTokenEntity = Token.builder()
			.token(newRefreshToken)
			.user(user)
			.isRevoked(false)
			.isExpired(false)
			.build();

		tokenRepository.save(newRefreshTokenEntity);

		log.info("RefreshToken 재발급 완료: userId={}", user.getId());

		return new LoginResponse(ResponseCode.SUCCESS, newAccessToken, refreshToken);
	}

	// 토큰 저장
	private void saveToken(String token, User user) {
		// 기존 토큰 무효화
		tokenRepository.existsByUserIdAndIsRevoked(user.getId());

		// 새로운 토큰 저장
		Token userToken = Token.builder()
			.token(token)
			.user(user)
			.isRevoked(false)
			.isExpired(false)
			.build();

		tokenRepository.save(userToken);
	}

	// 토큰 무효화
	private void revokeToken(String token) {
		log.debug("Token value to be revoked: {}", token);
		log.debug("Valid token query: {}", tokenRepository.findByToken(token));
		// 유효한 토큰 조회
		Token userToken = tokenRepository.findValidToken(token)
			.orElseThrow(() -> new CustomException(ResponseCode.TOKEN_IS_INVALID));

		// 토큰을 무효화
		userToken.setRevoked(true);
		tokenRepository.save(userToken);
	}
}
