package com.devkobe24.kobe_bulletin_board.domain.account.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.account.model.request.UpdateEmailRequest;
import com.devkobe24.kobe_bulletin_board.domain.account.model.response.UpdateEmailResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailUpdateService {

	private final UserRepository userRepository;

	@Transactional(transactionManager = "updateEmailTransactionManager")
	public UpdateEmailResponse updateEmail(UpdateEmailRequest request) {
		// 사용자 조회
		User user = userRepository.findById(request.getId())
			.orElseThrow(() -> {
				throw new CustomException(ResponseCode.USER_NOT_EXISTS);
			});

		// 이메일 수정.
		// 만약 새로운 이메일 입력값을 수정 전 이메일과 똑같은 값을 입력했을 경우 error
		if (user.getEmail().equals(request.getNewEmail())) {
			log.error("Request modify same email as new email {}", request.getNewEmail());
			return new UpdateEmailResponse(ResponseCode.TRY_MODIFY_SAME_EMAIL);
		}

		// 새로운 이메일 set
		user.setEmail(request.getNewEmail());
		// 저장
		userRepository.save(user);

		return new UpdateEmailResponse(ResponseCode.SUCCESS);
	}
}
