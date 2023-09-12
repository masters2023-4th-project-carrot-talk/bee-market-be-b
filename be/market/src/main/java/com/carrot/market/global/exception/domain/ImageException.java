package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageException implements CustomException {

	IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
	MAX_UPLOAD_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "이미지 업로드 크기가 초과됐습니다."),
	IMAGE_RESIZE_FAILED(HttpStatus.BAD_REQUEST, "이미지 사이즈 조정에 실패했습니다."),
	NOT_EXIST_IMAGE(HttpStatus.BAD_REQUEST, "이미지가 존재하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
