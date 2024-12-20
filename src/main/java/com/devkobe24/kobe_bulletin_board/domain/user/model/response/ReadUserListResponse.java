package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "유저 리스트 조회 Response")
@Getter
public class ReadUserListResponse {
	@Schema(description = "유저 목록")
	private final List<UserResponse> users;

	public ReadUserListResponse(List<User> userList) {
		this.users = userList.stream()
			.map(UserResponse::new)
			.collect(Collectors.toList());
	}
}
