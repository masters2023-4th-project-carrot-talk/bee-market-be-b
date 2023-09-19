package com.carrot.market.member.resolver;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class MemberId {
	@JsonProperty("memberId")
	private Integer memberID;

	public Long getMemberID() {
		if (memberID == null) {
			return null;
		}
		return memberID.longValue();

	}
}
