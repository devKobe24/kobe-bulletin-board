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
			.withSubject(password)
			.withClaim("nickname", nickname)
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(secretKey));
	}

	public static String createToken(String nickname, String email, UserRole role) {
		return JWT.create()
			.withSubject(nickname)
			.withClaim("email", email)
			.withClaim("role", role.getValue())
			.withIssuedAt(new Date())
			.withExpiresAt(new Date(System.currentTimeMillis() + tokenTimeForMinute * Constants.ON_MINUTE_TO_MILLIS))
			.sign(Algorithm.HMAC256(secretKey));
	}

	public static String createRefreshToken(String nickname, String email, UserRole role) {
		return JWT.create()
			.withSubject(nickname)
			.withClaim("email", email)
			.withClaim("role", role.getValue())
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

	// 토큰 값 추출
	public static String extractToken(String header) {
		log.debug("Authorization header: {}", header);
		// "Bearer "가 없는 경우 그대로 반환.
		if (StringUtils.hasText(header) && !header.startsWith("Bearer ")) {
			return header;
		}

		// "Bearer "가 포함된 경우 "Bearer "를 제거하고 반환.
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			return header.substring(7);
		}

		// 예외 처리.
		throw new IllegalArgumentException("Invalid Auth Header");
	}

	public static String getNickNameFromToken(String token) {
		try {
			// 토큰 디코딩
			DecodedJWT jwt = decodedJWT(token);

			// subject에서 사용자 정보를 추출
			String nickname = jwt.getSubject();

			// 로그 출력
			log.info("Extracted nickname : {}", nickname);

			return nickname;
		} catch (JWTVerificationException e) {
			log.error("Invalid token provided: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid token", e);
		}
	}

	public static String getEmailFromToken(String token) {
		DecodedJWT jwt = decodedJWT(token);
		return jwt.getClaim("email").asString();
	}

	public static String getUserRoleFromToken(String token) {
		try {
			// 토큰 디코딩
			DecodedJWT jwt = decodedJWT(token);

			// claim에서 UserRole 정보 추출
			String userRole = jwt.getClaim("role").asString();

			// 로그 출력
			log.info("Extracted userRole : {}", userRole);

			return userRole;
		} catch (JWTVerificationException e) {
			log.error("Invalid token provided: {}", e.getMessage());
			throw new IllegalArgumentException("Invalid token", e);
		}
	}
}
