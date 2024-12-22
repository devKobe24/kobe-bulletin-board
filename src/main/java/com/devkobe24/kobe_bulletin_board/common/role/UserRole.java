package com.devkobe24.kobe_bulletin_board.common.role;

public enum UserRole {
	USER("USER"),
	ADMIN("ADMIN");

	private final String value;

	UserRole(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
