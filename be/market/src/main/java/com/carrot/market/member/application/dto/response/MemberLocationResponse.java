package com.carrot.market.member.application.dto.response;

import com.carrot.market.location.domain.Location;
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

	public static MemberLocationResponse fromLocation(Location location) {
		return new MemberLocationResponse(location.getId(), location.getName(), true);
	}
}
