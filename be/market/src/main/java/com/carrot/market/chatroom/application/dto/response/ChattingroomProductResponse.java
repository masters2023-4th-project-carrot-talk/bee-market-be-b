package com.carrot.market.chatroom.application.dto.response;

import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;

public record ChattingroomProductResponse(
	Long id,
	String thumbnail
) {
	public static ChattingroomProductResponse from(ChatroomResponse chatroomResponse) {
		return new ChattingroomProductResponse(chatroomResponse.getProductId(), chatroomResponse.getProductMainImage());
	}
}
