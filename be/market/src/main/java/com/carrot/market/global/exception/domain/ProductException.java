package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductException implements CustomException {
	NOT_FOUND_ID(HttpStatus.BAD_REQUEST, "존재하지 않은 상품입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
