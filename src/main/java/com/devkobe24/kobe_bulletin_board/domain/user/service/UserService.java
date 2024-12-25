package com.devkobe24.kobe_bulletin_board.domain.user.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.repository.TokenRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserCredentialsRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.Token;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.UserCredentials;
import com.devkobe24.kobe_bulletin_board.domain.user.model.request.*;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.*;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import com.devkobe24.kobe_bulletin_board.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserCredentialsRepository userCredentialsRepository;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final Hasher hasher;

	// 1. 사용자 생성.
	@Transactional(transactionManager = "createUserTransactionManager")
	public CreateUserResponse createUser(CreateUserRequest request) {
		validateNickName(request.nickName());

		try {
			User newUser = buildUser(request.userName(), request.email(), request.nickName());
			UserCredentials credentials = buildUserCredentials(request.password(), newUser);
			newUser.setCredentials(credentials);

			User savedUser = userRepository.save(newUser);
			validateSavedUser(savedUser);

			return new CreateUserResponse(ResponseCode.SUCCESS, UserRole.USER.getValue());
		} catch (DataIntegrityViolationException e) {
			log.error("Data integrity violation: {}", e.getMessage());
			throw new CustomException(ResponseCode.USER_SAVED_FAILED, e.getMessage());
		}
	}

	private void validateNickName(String nickName) {
		if (userRepository.existsByNickName(nickName)) {
			throw new CustomException(ResponseCode.NICK_NAME_ALREADY_EXISTS);
		}
	}

	private User buildUser(String name, String email, String nickName) {
		return User.builder()
			.userName(name)
			.email(email)
			.nickName(nickName)
			.createdAt(new Timestamp(System.currentTimeMillis()))
			.build();
	}

	private UserCredentials buildUserCredentials(String password, User user) {
		return UserCredentials.builder()
			.user(user)
			.hashedPassword(hasher.getHashingValue(password))
			.role(UserRole.USER)
			.build();
	}

	private void validateSavedUser(User savedUser) {
		if (savedUser == null) {
			log.error("USER_SAVED_FAILED: User could not be saved");
			throw new CustomException(ResponseCode.USER_SAVED_FAILED);
		}
	}

	// 2. 특정 사용자 조회.
	@Transactional(transactionManager = "readSpecificUserTransactionManager")
	public ReadSpecificUserResponse readSpecificUser(ReadSpecificUserRequest request) {
		User user = userRepository.findById(request.id())
			.orElseThrow(() -> {
				log.error("User Not Found: id={}", request.id());
				return new CustomException(ResponseCode.USER_NOT_EXISTS);
			});
		return new ReadSpecificUserResponse(user);
	}

	// 3. 사용자 목록 조회
	@Transactional(transactionManager = "readUserListTransactionManager")
	public ReadUserListResponse readUserList() {
		List<User> users = userRepository.findAll();

		if (users.isEmpty()) {
			throw new CustomException(ResponseCode.USER_LIST_EMPTY);
		}

		return new ReadUserListResponse(users);
	}

	// 4. 사용자 정보 수정.
	@Transactional(transactionManager = "updateUserTransactionManger")
	public UpdateUserResponse updateUser(UpdateUserRequest request, Long id) {
		User user = findUserById(id);

		// 이름 수정
		if (request.getUserName() != null && !request.getUserName().equals(user.getUserName())) {
			user.setUserName(request.getUserName());
		}

		// 이메일 수정
		if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
			user.setEmail(request.getEmail());
		}

		// 닉네임 수정
		if (request.getNickName() != null && !request.getNickName().equals(user.getNickName())) {
			user.setNickName(request.getNickName());
		}

		userRepository.save(user);
		return new UpdateUserResponse(user);
	}

	@Transactional(transactionManager = "deleteUserTransactionManager")
	public DeleteUserResponse deleteUser(DeleteUserRequest request) {
		// 유저 조회
		User deleteTargetUser = findUserById(request);

		userRepository.delete(deleteTargetUser);

		return new DeleteUserResponse(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage());
	}

	// 유저 조회 메서드
	private User findUserById(DeleteUserRequest request) {
		// 유저 조회
		if (userCommonService.findById(request.id(), ResponseCode.USER_NOT_EXISTS) == null ) {
			log.error("User not found with id: {}", request.id());
		}

		return userCommonService.findById(request.id(), ResponseCode.SUCCESS);
	}

	// 역할 변경 시 토큰 갱신 트리거
	// 역할을 변경하는 메서드, 토큰을 갱신하도록 설정
	@Transactional(transactionManager = "changeUserRoleTransactionManager")
	public UpdateUserRoleResponse updateUserRole(UpdateUserRoleRequest request, Long userId ,String requestNewUserRole) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				log.error("User not found with userId: {}", userId);
				throw new CustomException(ResponseCode.USER_NOT_EXISTS);
			});

		UserCredentials credentials = userCredentialsRepository.findById(userId)
			.orElseThrow(() -> {
			log.error("USER_CREDENTIALS_NOT_FOUND");
			throw new CustomException(ResponseCode.USER_CREDENTIALS_NOT_EXISTS);
		});

		log.info("credential ==============>>>>>>>>>>>> {}", credentials);
		if (UserRole.USER.getValue().equals(requestNewUserRole)) {
			credentials.setRole(UserRole.USER);
		} else {
			credentials.setRole(UserRole.ADMIN);
		}
		userCredentialsRepository.save(credentials);

		String newUserToken = authService.refreshToken(userId);
		log.info("new user token: {}", newUserToken);
		String newUserRole = credentials.getRole().getValue();
		log.info("new user role: {}", newUserRole);

		return new UpdateUserRoleResponse(newUserRole, newUserToken);
	}
}
