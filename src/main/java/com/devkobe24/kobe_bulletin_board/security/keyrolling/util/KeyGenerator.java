package com.devkobe24.kobe_bulletin_board.security.keyrolling.util;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
	public static String generateKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[32]; // 256-bit key
		secureRandom.nextBytes(key);
		return Base64.getEncoder().encodeToString(key);
	}

	public static void main(String[] args) {
		System.out.println("Generated Key: " + generateKey());
	}
}
