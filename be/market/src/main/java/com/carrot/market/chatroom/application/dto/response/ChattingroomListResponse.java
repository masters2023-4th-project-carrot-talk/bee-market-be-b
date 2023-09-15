package com.carrot.market.chatroom.application.dto.response;

import java.time.LocalDateTime;

import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

public record ChattingroomListResponse(
	String nickname,
	String imageUrl,
	String locationName,
	String productMainImage,
	Long unreadChatCount,
	String latestChatContent,
	LocalDateTime createdAt
) {
	public ChattingroomListResponse(ChatroomResponse chatroomResponse, ChatroomInfo chatroomInfo) {
		this(chatroomResponse.getNickname()
			, chatroomResponse.getImageUrl()
			, chatroomResponse.getLocationName()
			, chatroomResponse.getProductMainImage()
			, chatroomInfo.unreadChatCount()
			, chatroomInfo.latestChatContent()
			, chatroomInfo.createdAt());
	}
}
