package com.carrot.market.chatroom.application.dto.response;

import java.time.LocalDateTime;

public record ChatroomInfo(
	Long chatRoomId,
	Long unreadChatCount,
	String lastChatContent,
	LocalDateTime createdAt
) {
}
