package com.devkobe24.kobe_bulletin_board.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 변경 Request")
public class UpdateUserRequest {
	@Schema(description = "수정할 유저 이름", example = "Kobe")
	@Size(max = 50, message = "유저 이름은 50자를 넘을 수 없습니다.")
	private String userName;

	@Schema(description = "수정할 유저 이메일", example = "user@example.com")
	@Email(message = "유효한 이메일 주소를 입력하세요.")
	private String email;

	@Schema(description = "수정할 유저 닉네임")
	@Size(max = 30, message = "유저 닉네임은 30자를 넘을 수 없습니다.")
	private String nickName;
}
