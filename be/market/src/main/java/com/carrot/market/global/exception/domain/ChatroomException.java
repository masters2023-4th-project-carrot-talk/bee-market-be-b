package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatroomException implements CustomException {
	NOT_FOUND_CHATROOM(HttpStatus.BAD_REQUEST, "존재하지 않은 채팅방입니다");
	private final HttpStatus httpStatus;
	private final String message;
}
