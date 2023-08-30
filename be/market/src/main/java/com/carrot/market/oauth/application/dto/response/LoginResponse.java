package com.carrot.market.oauth.application.dto.response;

import com.carrot.market.jwt.domain.Jwt;
import com.carrot.market.member.application.dto.response.LoginMemberResponse;

public record LoginResponse(
	boolean isUser,
	String accessToken,
	String refreshToken,
	LoginMemberResponse member
) {
	public static LoginResponse fail(String accessToken) {
		return new LoginResponse(false, accessToken, null, null);
	}

	public static LoginResponse success(Jwt jwt, LoginMemberResponse member) {
		return new LoginResponse(true, jwt.getAccessToken(), jwt.getRefreshToken(), member);
	}
}

