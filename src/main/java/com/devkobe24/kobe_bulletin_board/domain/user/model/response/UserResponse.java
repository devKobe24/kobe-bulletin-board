package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "유저 Response")
public class UserResponse {
	@Schema(description = "유저 id")
	private final Long id;

	@Schema(description = "유저 email")
	private final String email;

	@Schema(description = "유저 이름")
	private final String userName;

	@Schema(description = "유저 닉네임")
	private final String nickName;

	public UserResponse(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.userName = user.getUserName();
		this.nickName = user.getNickName();
	}
}
