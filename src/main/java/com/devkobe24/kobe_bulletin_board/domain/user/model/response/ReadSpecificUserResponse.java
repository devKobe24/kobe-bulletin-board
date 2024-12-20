package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "특정 유저 조회 Response")
public class ReadSpecificUserResponse {

	@Schema(description = "특정 유저 id", example = "1")
	private final Long id;

	@Schema(description = "특정 유저 email")
	private final String email;

	@Schema(description = "특정 유저 이름")
	private final String userName;

	@Schema(description = "특정 유저 닉네임")
	private final String nickName;

	public ReadSpecificUserResponse(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.userName = user.getUserName();
		this.nickName = user.getNickName();
	}
}
