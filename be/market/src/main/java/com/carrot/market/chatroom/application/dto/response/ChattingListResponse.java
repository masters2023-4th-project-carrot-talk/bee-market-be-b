package com.carrot.market.chatroom.application.dto.response;

import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

public record ChattingListResponse(
	String nickname,
	String imageUrl,
	String locationName,
	String productMainImage,
	Long unreadChatCount,
	String latestChatContent
) {
	public ChattingListResponse(ChatroomResponse chatroomResponse, ChatroomInfo chatroomInfo) {
		this(chatroomResponse.getNickname()
			, chatroomResponse.getImageUrl()
			, chatroomResponse.getLocationName()
			, chatroomResponse.getProductMainImage()
			, chatroomInfo.unreadChatCount()
			, chatroomInfo.latestChatContent());
	}
}
