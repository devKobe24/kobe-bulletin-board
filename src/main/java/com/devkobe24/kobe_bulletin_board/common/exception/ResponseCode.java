package com.devkobe24.kobe_bulletin_board.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode implements CodeInterface {
	SUCCESS(0, "SUCCESS"),

	USER_ALREADY_EXISTS(-1,"USER_ALREADY_EXIST"),
	USER_SAVED_FAILED(-2, "USER_SAVED_FAILED");

	private final Integer code;
	private final String message;
}
