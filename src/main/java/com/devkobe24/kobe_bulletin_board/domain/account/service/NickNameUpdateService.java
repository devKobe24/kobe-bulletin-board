package com.devkobe24.kobe_bulletin_board.domain.account.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.account.model.request.UpdateNickNameRequest;
import com.devkobe24.kobe_bulletin_board.domain.account.model.response.UpdateNickNameResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NickNameUpdateService {

	private final UserRepository userRepository;

	@Transactional(transactionManager = "updateNickNameTransactionManager")
	public UpdateNickNameResponse updateNickName(UpdateNickNameRequest request) {
		User user = userRepository.findById(request.getId())
			.orElseThrow(() -> {
				log.warn("User not found with id {}", request.getId());
				throw new CustomException(ResponseCode.USER_NOT_EXISTS);
			});

		String newNickName = request.getNewNickName();
		if (user.getNickName().equals(newNickName)) {
			log.warn("Nickname is already taken");
			throw new CustomException(ResponseCode.ALREADY_TAKEN_NICKNAME);
		}

		user.setNickName(newNickName);
		userRepository.save(user);
		return new UpdateNickNameResponse(ResponseCode.SUCCESS);
	}
}
