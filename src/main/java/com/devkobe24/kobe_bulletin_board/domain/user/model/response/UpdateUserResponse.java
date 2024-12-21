package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "수정할 유저 Response")
public class UpdateUserResponse {

	@Schema(description = "유저 이름")
	private String userName;

	@Schema(description = "유저 이메일")
	private String email;

	@Schema(description = "유저 닉네임")
	private String nickName;

	public UpdateUserResponse(User user) {
		this.userName = user.getUserName();
		this.email = user.getEmail();
		this.nickName = user.getNickName();
	}
}
