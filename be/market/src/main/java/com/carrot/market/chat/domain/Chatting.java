package com.carrot.market.chat.domain;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.carrot.market.chat.presentation.dto.Message;

import jakarta.persistence.Id;
import lombok.Builder;

@Document(collection = "chatting")
@Builder
public class Chatting {
	@Id
	private String id;
	private Long chatRoomNo;
	private Long senderId;
	private String content;
	private LocalDateTime createdAt;

	public static Chatting from(Message message) {
		return Chatting.builder()
			.chatRoomNo(message.getChatroomId())
			.content(message.getContent())
			.senderId(message.getSenderId())
			.createdAt(LocalDateTime.now())
			.build();
	}

}
