package com.carrot.market.chatroom.application.dto.response;

import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

public record ChattingroomOpponentResponse(
	Long id,
	String nickname,
	String imageUrl
) {
	public static ChattingroomOpponentResponse from(ChatroomResponse chatroomResponse) {
		return new ChattingroomOpponentResponse(chatroomResponse.getMemberId(), chatroomResponse.getNickname(),
			chatroomResponse.getImageUrl());
	}
}
