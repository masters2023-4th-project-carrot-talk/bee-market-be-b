package com.carrot.market.member.application.dto.response;

import com.carrot.market.member.domain.Member;

public record LoginMemberResponse(
	Long id,
	String nickname,
	String imageUrl
) {
	public static LoginMemberResponse from(Member member) {
		return new LoginMemberResponse(member.getId(), member.getNickname(), member.getImageUrl());
	}
}

