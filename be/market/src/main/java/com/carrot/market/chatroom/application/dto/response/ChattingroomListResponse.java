package com.carrot.market.chatroom.application.dto.response;

import java.time.LocalDateTime;

import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

public record ChattingroomListResponse(
	ChattingroomOpponentResponse opponent,
	ChattingroomProductResponse product,
	Long unreadChatCount,
	String lastChatContent,
	Long chatroomId,
	LocalDateTime lastChatTime
) {
	public ChattingroomListResponse(ChatroomResponse chatroomResponse, ChatroomInfo chatroomInfo) {
		this(
			ChattingroomOpponentResponse.from(chatroomResponse)
			, ChattingroomProductResponse.from(chatroomResponse)
			, chatroomInfo.unreadChatCount()
			, chatroomInfo.lastChatContent()
			, chatroomInfo.chatRoomId()
			, chatroomInfo.createdAt());
	}
}
