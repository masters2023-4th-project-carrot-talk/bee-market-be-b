package com.carrot.market.global.exception.domain;

import org.springframework.http.HttpStatus;

public interface CustomException {
	HttpStatus getHttpStatus();

	String getMessage();
}
