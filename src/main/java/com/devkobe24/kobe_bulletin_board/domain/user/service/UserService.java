package com.devkobe24.kobe_bulletin_board.domain.user.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.UserCredentials;
import com.devkobe24.kobe_bulletin_board.domain.user.model.request.CreateUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.user.model.request.ReadSpecificUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.CreateUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.ReadSpecificUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.ReadUserListResponse;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
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

	@Transactional(transactionManager = "readSpecificUserTransactionManager")
	public ReadSpecificUserResponse readSpecificUser(ReadSpecificUserRequest request) {
		try {
			User findUser = userRepository.findById(request.id())
				.orElseThrow(() -> {
					log.error("User not found with id: {}", request.id());
					return new CustomException(ResponseCode.USER_NOT_EXISTS);
				});

			return new ReadSpecificUserResponse(findUser);
		} catch (Exception e) {
			log.error("Unexpected error while reading specific user: {}", e.getMessage());
			throw new CustomException(ResponseCode.USER_READ_FAILED, e.getMessage());
		}
	}

	@Transactional(transactionManager = "readUserListTransactionManager")
	public ReadUserListResponse readUserList() {
		List<User> users = userRepository.findAll();

		if (users.isEmpty()) {
			log.warn("No users found in the database");
			throw new CustomException(ResponseCode.USER_LIST_EMPTY);
		}

		return new ReadUserListResponse(users);
	}
}
