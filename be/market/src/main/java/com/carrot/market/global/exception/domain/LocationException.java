package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationException implements CustomException {

	NOT_FOUND_ID(HttpStatus.BAD_REQUEST, "존재하지 않은 동네입니다.");

	private final HttpStatus httpStatus;
	private final String message;

}
