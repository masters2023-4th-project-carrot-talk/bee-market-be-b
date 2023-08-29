package com.carrot.market.member.application.dto.request;

import com.carrot.market.member.domain.Member;

import lombok.Builder;

public record SignupServiceRequest(
	String socialId,
	String imageUrl,
	String nickname,
	Long mainLocationId,
	Long subLocationId
) {
	@Builder
	public SignupServiceRequest {
	}

	public Member toMember() {
		return Member.builder()
			.socialId(this.socialId())
			.imageUrl(this.imageUrl())
			.nickname(this.nickname())
			.build();
	}
}
