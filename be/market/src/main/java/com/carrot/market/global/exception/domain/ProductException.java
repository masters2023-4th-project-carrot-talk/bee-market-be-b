package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductException implements CustomException {
	NOT_FOUND_ID(HttpStatus.BAD_REQUEST, "존재하지 않은 상품입니다."),
	NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "존재하지 않는 카테고리입니다."),
	NOT_AUTHORIZAED_UPDATE(HttpStatus.UNAUTHORIZED, "상품을 수정할 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
