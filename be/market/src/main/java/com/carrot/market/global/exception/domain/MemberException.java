package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberException implements CustomException {
	NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."),
	NOT_REGISTER_LOCATION(HttpStatus.BAD_REQUEST, "새로운 동네를 추가할 수 없습니다."),
	NOT_REMOVE_LOCATION(HttpStatus.BAD_REQUEST, "새로운 동네를 삭제할 수 없습니다."),
	EXIST_MEMBER(HttpStatus.BAD_REQUEST, "같은 닉네임이 존재합니다.");

	private final HttpStatus httpStatus;
	private final String message;

}
