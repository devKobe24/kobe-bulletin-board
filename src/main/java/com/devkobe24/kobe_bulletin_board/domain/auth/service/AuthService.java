package com.devkobe24.kobe_bulletin_board.domain.auth.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.CreateUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.LoginRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.CreateUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.LoginResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.UserCredentials;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final Hasher hasher;

	@Transactional(transactionManager = "createUserTransactionManager")
	public CreateUserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByNickName(request.nickName())) {
			throw new CustomException(ResponseCode.NICK_NAME_ALREADY_EXISTS);
		}

		try {
			User newUser = this.newUser(request.userName(), request.email(), request.nickName());
			UserCredentials newCredentials = this.newUserCredentials(request.password(), newUser);
			newUser.setCredentials(newCredentials);

			User savedUser = userRepository.save(newUser);

			validateSavedUser(savedUser);
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.USER_SAVED_FAILED, e.getMessage());
		} catch (Exception e) {
			log.error("Unexpected error occurred: {}", e.getMessage());
			throw new CustomException(ResponseCode.USER_SAVED_FAILED, e.getMessage());
		}
		return new CreateUserResponse(ResponseCode.SUCCESS);
	}

	private User newUser(String name, String email, String nickName) {
		User newUser = User.builder()
			.userName(name)
			.email(email)
			.nickName(nickName)
			.createdAt(new Timestamp(System.currentTimeMillis()))
			.build();

		return newUser;
	}

	private UserCredentials newUserCredentials(String password, User user) {
		String hashedValue = hasher.getHashingValue(password);

		UserCredentials credentials = UserCredentials.builder()
			.user(user)
			.hashedPassword(hashedValue)
			.role(UserRole.USER) // 가입시 모든 유저/관리자는 USER로 role을 부여 받고 관리자는 추후에 DB에서 따로 role을 ADMIN으로 수정하여 바뀌는 시스템.
			.build();

		return credentials;
	}

	private void validateSavedUser(User savedUser) {
		if (savedUser == null) {
			log.error("USER_SAVED_FAILED: User could not be saved");
			throw new CustomException(ResponseCode.USER_SAVED_FAILED);
		}
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

		String token = JWTProvider.createToken(loggedInUser.getNickName(), loggedInUser.getEmail());
		return new LoginResponse(ResponseCode.SUCCESS, token);
	}
}
