package com.devkobe24.kobe_bulletin_board.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final CodeInterface codeInterface;

	public CustomException(CodeInterface codeInterface) {
		super(codeInterface.getMessage());
		this.codeInterface = codeInterface;
	}

	public CustomException(CodeInterface codeInterface, String message) {
		super(codeInterface.getMessage() + message);
		this.codeInterface = codeInterface;
	}
}
