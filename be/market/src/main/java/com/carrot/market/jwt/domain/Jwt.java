package com.carrot.market.jwt.domain;

public record Jwt(
	String accessToken,
	String refreshToken
) {
}
