package com.devkobe24.kobe_bulletin_board.domain.auth.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.request.CreateUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.auth.model.response.CreateUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.UserCredentials;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
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
		Optional<User> user = userRepository.findByUserName(request.userName());

		if (user.isPresent()) {
			log.error("USER_ALREADY_EXISTS: {}", request.userName());
			throw new CustomException(ResponseCode.USER_ALREADY_EXISTS);
		}

		try {
			User newUser = this.newUser(request.userName(), request.email());
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

	private User newUser(String name, String email) {
		User newUser = User.builder()
			.userName(name)
			.email(email)
			.createdAt(new Timestamp(System.currentTimeMillis()))
			.build();

		return newUser;
	}

	private UserCredentials newUserCredentials(String password, User user) {
		String hashedValue = hasher.getHashingValue(password);

		UserCredentials credentials = UserCredentials.builder()
			.user(user)
			.hashedPassword(hashedValue)
			.build();

		return credentials;
	}

	private void validateSavedUser(User savedUser) {
		if (savedUser == null) {
			log.error("USER_SAVED_FAILED: User could not be saved");
			throw new CustomException(ResponseCode.USER_SAVED_FAILED);
		}
	}
}
