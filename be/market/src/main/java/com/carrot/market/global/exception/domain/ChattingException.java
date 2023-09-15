package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChattingException implements CustomException {
	INVALID_CHATTING_ID(HttpStatus.BAD_REQUEST, "존재하지 않은 채팅입니다");
	private final HttpStatus httpStatus;
	private final String message;
}
