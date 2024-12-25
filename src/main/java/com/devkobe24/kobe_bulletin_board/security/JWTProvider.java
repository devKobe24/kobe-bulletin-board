package com.devkobe24.kobe_bulletin_board.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.devkobe24.kobe_bulletin_board.common.constants.Constants;
import com.devkobe24.kobe_bulletin_board.common.exception.CustomException;
import com.devkobe24.kobe_bulletin_board.common.exception.ResponseCode;
import com.devkobe24.kobe_bulletin_board.common.role.UserRole;
import com.devkobe24.kobe_bulletin_board.domain.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

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

	public static String createPostToken(User userName, UserRole userRole) {
		return JWT.create()
			.withSubject(userName.getUserName())
			.withClaim("userRole", userRole.getValue())
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(secretKey));
	}

	public static String createAccessToken(User user, UserRole role) {
		return JWT.create()
			.withSubject(user.getEmail())
			.withClaim("username", user.getUserName())
			.withClaim("role", role.getValue())
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(secretKey));
	}

	public static String createRefreshToken(User user, UserRole role) {
		return JWT.create()
			.withSubject(user.getEmail())
			.withClaim("role", role.getValue())
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
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

	// 토큰 값 추출
	public static String extractToken(String header) {
		log.debug("Authorization header: {}", header);

		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			String token = header.substring(7).trim();
			log.debug("Extracted token: {}", token);
			return token;
		}

		throw new IllegalArgumentException("Invalid Authorization header");
	}

	public static String getNickNameFromToken(String token) {
		try {
			log.debug("Token before decoding nickname: {}", token);
			DecodedJWT jwt = decodedJWT(token);
			String nickname = jwt.getClaim("nickname").asString();
			log.debug("Decoded nickname: {}", nickname);
			return nickname;
		} catch (JWTDecodeException e) {
			log.error("Invalid nickname token format: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid nickname token format", e);
		} catch (Exception e) {
			log.error("Unexpected error occurred while decoding nickname: {}", e.getMessage());
			throw new IllegalArgumentException("Unexpected error occurred while decoding nickname", e);
		}
	}

	public static String getEmailFromToken(String token) {
		DecodedJWT jwt = decodedJWT(token);
		return jwt.getClaim("email").asString();
	}

	public static String getUserRoleFromToken(String token) {

		try {
			log.debug("Token before decoding: {}", token);
			DecodedJWT jwt = decodedJWT(token);
			String role = jwt.getClaim("role").asString();
			log.debug("Decoded Role: {}", role);
			return role;
		} catch (JWTDecodeException e) {
			log.error("Invalid token format: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid token format", e);
		} catch (Exception e) {
			log.error("Unexpected token error: {}", e.getMessage());
			throw new IllegalArgumentException("Unexpected token error", e);
		}
	}
}
