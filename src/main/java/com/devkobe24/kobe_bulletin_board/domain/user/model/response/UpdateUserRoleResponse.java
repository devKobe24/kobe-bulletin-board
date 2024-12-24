package com.devkobe24.kobe_bulletin_board.domain.user.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "유저 역할(권한) 변경 Response")
public class UpdateUserRoleResponse {

	@Schema(description = "변경된 역할(권한)", example = "'USER' 또는 'ADMIN'")
	private String userRole;

	@Schema(description = "변경된 유저 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6Ik...")
	private String userToken;

	public UpdateUserRoleResponse(String userRole, String userToken) {
		if (userRole == null) {
			throw new IllegalArgumentException("userRole cannot be null");
		}
		if (userToken == null) {
			throw new IllegalArgumentException("token cannot be null");
		}
		this.userRole = userRole;
		this.userToken = userToken;
	}
}
