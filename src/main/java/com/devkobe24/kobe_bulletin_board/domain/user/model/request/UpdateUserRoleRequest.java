package com.devkobe24.kobe_bulletin_board.domain.user.model.request;

import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "유저 역할(권한) 변경 Request")
public class UpdateUserRoleRequest {
	@Schema(description = "변경할 유저의 ID", example = "1")
	private Long userId;

	@Schema(description = "변경할 역할", example = "'USER' 또는 'ADMIN'")
	private UserRole newRole;
}
