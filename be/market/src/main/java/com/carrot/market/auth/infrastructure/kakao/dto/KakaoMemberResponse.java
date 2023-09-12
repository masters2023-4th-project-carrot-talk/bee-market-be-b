package com.carrot.market.auth.infrastructure.kakao.dto;

import java.time.LocalDateTime;

import com.carrot.market.auth.domain.OauthMember;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoMemberResponse(
	Long id,
	boolean hasSignedUp,
	LocalDateTime connectedAt,
	KakaoAccount kakaoAccount
) {

	public OauthMember toDomain() {
		return new OauthMember(String.valueOf(id), kakaoAccount().profile().profileImageUrl());

	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record KakaoAccount(
		Profile profile
	) {
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record Profile(
		String profileImageUrl
	) {
	}
}
