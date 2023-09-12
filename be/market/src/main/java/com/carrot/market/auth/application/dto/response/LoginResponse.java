package com.carrot.market.auth.application.dto.response;

import com.carrot.market.jwt.domain.Jwt;
import com.carrot.market.member.application.dto.response.LoginMemberResponse;

public record LoginResponse(
	boolean isUser,
	String accessToken,
	String refreshToken,
	LoginMemberResponse user
) {
	public static LoginResponse fail(String accessToken) {
		return new LoginResponse(false, accessToken, null, null);
	}

	public static LoginResponse success(Jwt jwt, LoginMemberResponse member) {
		return new LoginResponse(true, jwt.accessToken(), jwt.refreshToken(), member);
	}
}

