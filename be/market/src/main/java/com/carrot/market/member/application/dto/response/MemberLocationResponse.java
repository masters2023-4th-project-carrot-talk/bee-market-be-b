package com.carrot.market.member.application.dto.response;

import com.carrot.market.member.domain.MemberLocation;

public record MemberLocationResponse(
	Long id,
	String name,
	boolean isMainLocation
) {

	public static MemberLocationResponse from(MemberLocation memberLocation) {
		return new MemberLocationResponse(
			memberLocation.getLocation().getId(),
			memberLocation.getLocation().getName(),
			memberLocation.isMain()
		);
	}
}
