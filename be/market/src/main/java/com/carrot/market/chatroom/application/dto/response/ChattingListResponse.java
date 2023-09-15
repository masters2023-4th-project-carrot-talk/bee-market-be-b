package com.carrot.market.chatroom.application.dto.response;

import java.time.LocalDateTime;

import com.carrot.market.chat.domain.Chatting;

import lombok.Builder;

@Builder
public record ChattingListResponse(
	String chattingId,
	String content,
	LocalDateTime createdAt,
	Long senderId
) {
	public ChattingListResponse(Chatting chatting) {
		this(chatting.getId(), chatting.getContent(), chatting.getCreatedAt(), chatting.getSenderId());
	}
}
