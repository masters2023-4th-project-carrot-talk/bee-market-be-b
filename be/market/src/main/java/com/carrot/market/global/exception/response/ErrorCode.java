package com.carrot.market.global.exception.response;

public record ErrorCode(
	int status,
	String message
) {
}
