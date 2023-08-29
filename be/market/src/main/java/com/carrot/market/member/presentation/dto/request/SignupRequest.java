package com.carrot.market.member.presentation.dto.request;

import com.carrot.market.member.application.dto.request.SignupServiceRequest;

public record SignupRequest(
	String nickname,
	Long mainLocationId,
	Long subLocationId
) {
	public SignupServiceRequest toSignupServiceRequest(String imageUrl, String socialId) {
		return SignupServiceRequest.builder()
			.nickname(this.nickname)
			.mainLocationId(this.mainLocationId)
			.subLocationId(this.subLocationId)
			.imageUrl(imageUrl)
			.socialId(socialId)
			.build();
	}
}