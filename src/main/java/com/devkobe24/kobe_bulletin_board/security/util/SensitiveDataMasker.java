package com.devkobe24.kobe_bulletin_board.security.util;

public class SensitiveDataMasker {
	/**
	 * 토큰 문자열을 마스킹합니다.
	 * @param token 원본 토큰 문자열
	 * @return 마스킹된 토큰 문자열
	 */
	public static String maskToken(String token) {
		if (token == null || token.length() < 10) {
			return "*******";
		}
		return token.substring(0, 5) + "*****" + token.substring(token.length() - 5);
	}

	/**
	 * 비밀번호를 마스킹합니다.
	 * @param password 원본 비밀번호
	 * @return 마스킹된 비밀번호
	 */
	public static String maskPassword(String password) {
		return "*******";
	}
}
