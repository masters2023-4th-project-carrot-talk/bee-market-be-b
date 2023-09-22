package com.carrot.market.chatroom.application.dto.response;

import com.carrot.market.member.domain.Member;

public record ChattingOpponentResponse(
	Long id,
	String nickname
) {
	public static ChattingOpponentResponse from(Member member) {
		return new ChattingOpponentResponse(member.getId(), member.getNickname());
	}
}
