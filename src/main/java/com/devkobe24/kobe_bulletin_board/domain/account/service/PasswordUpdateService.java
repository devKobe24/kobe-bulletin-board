package com.devkobe24.kobe_bulletin_board.domain.account.service;

import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.domain.account.model.request.UpdatePasswordRequest;
import com.devkobe24.kobe_bulletin_board.domain.account.model.response.UpdatePasswordResponse;
import com.devkobe24.kobe_bulletin_board.domain.repository.UserRepository;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import com.devkobe24.kobe_bulletin_board.security.Hasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordUpdateService {

	private final UserRepository userRepository;
	private final Hasher hasher;

	@Transactional(transactionManager = "updatePasswordTransactionManager")
	public UpdatePasswordResponse updatePassword(UpdatePasswordRequest request) {
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

		return new UpdatePasswordResponse(ResponseCode.SUCCESS);
	}
}
