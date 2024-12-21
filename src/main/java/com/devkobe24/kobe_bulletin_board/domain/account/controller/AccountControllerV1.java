package com.devkobe24.kobe_bulletin_board.domain.account.controller;

import com.devkobe24.kobe_bulletin_board.domain.account.model.request.UpdateEmailRequest;
import com.devkobe24.kobe_bulletin_board.domain.account.model.request.UpdatePasswordRequest;
import com.devkobe24.kobe_bulletin_board.domain.account.model.response.UpdateEmailResponse;
import com.devkobe24.kobe_bulletin_board.domain.account.model.response.UpdatePasswordResponse;
import com.devkobe24.kobe_bulletin_board.domain.account.service.EmailUpdateService;
import com.devkobe24.kobe_bulletin_board.domain.account.service.PasswordUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account API", description = "V1 Account API")
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountControllerV1 {

	private final PasswordUpdateService passwordUpdateService;
	private final EmailUpdateService emailUpdateService;

	@Operation(
		summary = "비밀번호 수정 및 변경",
		description = "사용자의 비밀번호를 수정 및 변경합니다."
	)
	@PatchMapping("/update/password")
	public UpdatePasswordResponse updatePassword(
		@RequestBody @Valid UpdatePasswordRequest request
	) {
		return passwordUpdateService.updatePassword(request);
	}

	@Operation(
		summary = "이메일 수정 및 변경",
		description = "사용자의 이메일을 수정 및 변경합니다."
	)
	@PatchMapping("/update/email")
	public UpdateEmailResponse updateEmail(
		@RequestBody @Valid UpdateEmailRequest request
	) {
		return emailUpdateService.updateEmail(request);
	}
}
