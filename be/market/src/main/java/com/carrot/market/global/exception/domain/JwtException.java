package com.carrot.market.global.exception.domain;

import java.security.SignatureException;
import java.util.Arrays;

import org.springframework.http.HttpStatus;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;

@Getter
public enum JwtException implements CustomException {

	EXPIRED_JWT_EXCEPTION(ExpiredJwtException.class, "만료된 토큰입니다."),
	MALFORMED_JWT_EXCEPTION(MalformedJwtException.class, "잘못된 형식의 토큰입니다."),
	SIGNATURE_EXCEPTION(SignatureException.class, "토큰의 키가 올바르지 않습니다."),
	UNSUPPORTED_JWT_EXCEPTION(UnsupportedJwtException.class, "지원하지 않는 토큰입니다."),
	ILLEGAL_ARGUMENT_EXCEPTION(IllegalArgumentException.class, "유효하지 않은 토큰입니다.");

	private final Class<? extends Exception> exceptionType;
	private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
	private final String message;

	JwtException(Class<? extends Exception> exceptionType, String message) {
		this.exceptionType = exceptionType;
		this.message = message;
	}

	public static JwtException from(Exception ex) {
		return Arrays.stream(values())
			.filter(exception -> exception.getExceptionType() == ex.getClass())
			.findFirst()
			.orElse(ILLEGAL_ARGUMENT_EXCEPTION);
	}

}
