package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OauthException implements CustomException {
	UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다");

	private final HttpStatus httpStatus;
	private final String message;

}
