package com.devkobe24.kobe_bulletin_board.security;

import java.util.Date;

import com.auth0.jwt.exceptions.*;
import com.devkobe24.kobe_bulletin_board.common.constants.Constants;
import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTProvider {
	private static String secretKey;
	private static String refreshSecretKey;
	private static long tokenTimeForMinute;
	private static long refreshTokenTimeForMinute;

	@Value("${token.secret-key}")
	public void setSecretKey(String secretKey) {
		JWTProvider.secretKey = secretKey;
	}

	@Value("${token.refresh-secret-key}")
	public void setRefreshSecretKey(String refreshSecretKey) {
		JWTProvider.refreshSecretKey = refreshSecretKey;
	}

	@Value("${token.token-time}")
	public void setTokenTime(long tokenTime) {
		JWTProvider.tokenTimeForMinute = tokenTime;
	}

	@Value("${token.refresh-token-time}")
	public void setRefreshTokenTime(long refreshTokenTime) {
		JWTProvider.tokenTimeForMinute = refreshTokenTime;
	}

	public static String createToken(String subject) {
		return JWT.create()
			.withSubject(subject)
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(secretKey));
	}

	public static String createRefreshToken(String email, String nickname) {
		return JWT.create()
			.withSubject(nickname)
			.withClaim("email", email)
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(refreshSecretKey));
	}

	public static DecodedJWT checkTokenForRefresh(String token) {
		try {
			DecodedJWT decoded = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
			log.error("token must be expired : {}", decoded.getSubject());
			throw new CustomException(ResponseCode.ACCESS_TOKEN_IS_NOT_EXPIRED);
		} catch (AlgorithmMismatchException | SignatureVerificationException | InvalidClaimException e) {
			throw new CustomException(ResponseCode.TOKEN_IS_INVALID);
		} catch (TokenExpiredException e) {
			return JWT.decode(token);
		}
	}

	public static DecodedJWT decodeAccessToken(String token) {
		return decodeTokenAfterVerify(token, secretKey);
	}

	public static DecodedJWT decodeRefreshToken(String token) {
		return decodeTokenAfterVerify(token, refreshSecretKey);
	}

	private static DecodedJWT decodeTokenAfterVerify(String token, String key) {
		try {
			return JWT.require(Algorithm.HMAC256(key)).build().verify(token);
		} catch (AlgorithmMismatchException | SignatureVerificationException | InvalidClaimException e) {
			throw new CustomException(ResponseCode.TOKEN_IS_INVALID);
		}catch (TokenExpiredException e) {
			throw new CustomException(ResponseCode.TOKEN_IS_EXPIRED);
		}
	}

	public static DecodedJWT decodedJWT(String token) {
		return JWT.decode(token);
	}

	public static String extractToken(String header) {
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			return header.substring(7);
		}else {
			throw new IllegalArgumentException("Invalid Auth Header");
		}
	}

	public static String getUserFromToken(String token) {
		DecodedJWT jwt = decodedJWT(token);
		return jwt.getSubject();
	}


}
