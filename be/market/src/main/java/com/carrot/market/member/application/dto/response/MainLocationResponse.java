package com.carrot.market.member.application.dto.response;

import com.carrot.market.member.domain.MemberLocation;

public record MainLocationResponse(
	Long mainLocationId
) {
	public MainLocationResponse(MemberLocation memberLocation) {
		this(memberLocation.getLocation().getId());
	}
}
