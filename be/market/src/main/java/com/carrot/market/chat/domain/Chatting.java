package com.carrot.market.chat.domain;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.carrot.market.chat.presentation.dto.Message;

import jakarta.persistence.Id;
import lombok.Builder;

@Document(collection = "chatting")
public class Chatting {
	@Id
	private String id;
	private Long chatRoomNo;
	private Long senderId;
	private String content;
	private LocalDateTime createdAt;

	@Builder
	public Chatting(Long chatRoomNo, Long senderId, String content) {
		this.chatRoomNo = chatRoomNo;
		this.senderId = senderId;
		this.content = content;
		this.createdAt = LocalDateTime.now();
	}

	public static Chatting from(Message message) {
		return Chatting.builder()
			.chatRoomNo(message.getChatroomId())
			.content(message.getContent())
			.senderId(message.getSenderId())
			.build();
	}

}
