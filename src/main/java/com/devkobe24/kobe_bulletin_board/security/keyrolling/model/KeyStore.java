package com.devkobe24.kobe_bulletin_board.security.keyrolling.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyStore {
	private String secretKey;
	private String previousSecretKey;
}
