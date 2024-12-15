package com.devkobe24.kobe_bulletin_board.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode implements CodeInterface {
	SUCCESS(0, "SUCCESS"),

	USER_ALREADY_EXISTS(-1,"USER_ALREADY_EXIST"),
	USER_SAVED_FAILED(-2, "USER_SAVED_FAILED"),
	USER_NOT_EXISTS(-3,"USER_NOT_EXISTS"),

	MISS_MATCH_PASSWORD(-4,"MISS_MATCH_PASSWORD"),

	ACCESS_TOKEN_IS_NOT_EXPIRED(-5,"ACCESS_TOKEN_IS_NOT_EXPIRED"),
	TOKEN_IS_INVALID(-6,"TOKEN_IS_INVALID"),
	TOKEN_IS_EXPIRED(-7,"TOKEN_IS_EXPIRED");

	private final Integer code;
	private final String message;
}
