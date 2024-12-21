package com.devkobe24.kobe_bulletin_board.domain.user.controller;

import com.devkobe24.kobe_bulletin_board.domain.user.model.request.CreateUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.user.model.request.ReadSpecificUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.user.model.request.UpdateUserRequest;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.CreateUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.ReadSpecificUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.ReadUserListResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.model.response.UpdateUserResponse;
import com.devkobe24.kobe_bulletin_board.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "V1 User API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserControllerV1 {

	private final UserService userService;

	@Operation(
		summary = "새로운 유저를 생성합니다.",
		description = "새로운 유저의 정보를 생성하여 저장합니다."
	)
	@PostMapping("/create-user")
	public CreateUserResponse createUser(
		@RequestBody @Valid CreateUserRequest request
	) {
		return userService.createUser(request);
	}

	@Operation(
		summary = "특정 유저를 조회합니다.",
		description = "ID로 특정 유저를 조회합니다."
	)
	@GetMapping("/{id}")
	public ReadSpecificUserResponse readSpecificUser(@PathVariable("id") Long id) {
		ReadSpecificUserRequest request = new ReadSpecificUserRequest(id);

		ReadSpecificUserResponse response = userService.readSpecificUser(request);

		return response;
	}

	@Operation(
		summary = "전체 유저를 조회합니다.",
		description = "DB에 저장된 전체 유저를 조회합니다."
	)
	@GetMapping("/list")
	public ReadUserListResponse readUserList() {
		ReadUserListResponse response = userService.readUserList();
		return response;
	}

	@Operation(
		summary = "유저 수정.",
		description = "유저 이름 필드만 수정합니다."
	)
	@PatchMapping("/{id}/name")
	public UpdateUserResponse updateUserName(
		@PathVariable Long id,
		@RequestParam String userName
	) {
		UpdateUserRequest request = new UpdateUserRequest();
		request.setUserName(userName);

		UpdateUserResponse updateUserName = userService.updateUser(request, id);
		return updateUserName;
	}
}