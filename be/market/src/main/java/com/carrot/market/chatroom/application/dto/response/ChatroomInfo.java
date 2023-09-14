package com.carrot.market.chatroom.application.dto.response;

public record ChatroomInfo(
	Long chatRoomId,
	Long unreadChatCount,
	String latestChatContent
) {
}
