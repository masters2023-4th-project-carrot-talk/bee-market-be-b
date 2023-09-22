package com.carrot.market.chatroom.infrastructure.dto;

public interface ChatroomResponse {
	Long getMemberId();

	String getNickname();

	String getImageUrl();

	Long getProductId();

	String getProductMainImage();

	Long getChatroomId();

}
