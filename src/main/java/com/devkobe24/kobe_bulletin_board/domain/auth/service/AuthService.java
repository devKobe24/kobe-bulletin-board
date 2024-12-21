package com.devkobe24.kobe_bulletin_board.domain.auth.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LoginRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LogoutRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.ModifyPasswordRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LoginResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LogoutResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.ModifyPasswordResponse;
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

		// 새 토큰 생성 및 저장
		String token = JWTProvider.createToken(user.getNickName(), user.getEmail());
		saveToken(token, user);

		return new LoginResponse(ResponseCode.SUCCESS, token);
	}

	@Transactional(transactionManager = "logoutTransactionManager")
	public LogoutResponse logout(LogoutRequest request) {
		log.debug("Request token: {}", request.token());

		String extractedToken = JWTProvider.extractToken(request.token());
		log.debug("Extracted token: {}", extractedToken);
		log.debug("Token in database: {}", tokenRepository.findByToken(extractedToken));

		revokeToken(extractedToken);

		return new LogoutResponse(ResponseCode.SUCCESS);
	}

	@Transactional(transactionManager = "modifyPasswordTransactionManager")
	public ModifyPasswordResponse modifyPassword(ModifyPasswordRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_EXISTS));

		// 비밀번호 일치여부 확인
		String hashedValue = hasher.getHashingValue(request.getPreviousPassword());
		if (!user.getUserCredentials().getHashedPassword().equals(hashedValue)) {
			throw new CustomException(ResponseCode.MISS_MATCH_PASSWORD);
		}

		String newHashedValue = hasher.getHashingValue(request.getNewPassword());

		// 사용자 비밀번호 업데이트
		user.getUserCredentials().setHashedPassword(newHashedValue);
		userRepository.save(user); // 변경사항 저장

		return new ModifyPasswordResponse(ResponseCode.SUCCESS);
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
