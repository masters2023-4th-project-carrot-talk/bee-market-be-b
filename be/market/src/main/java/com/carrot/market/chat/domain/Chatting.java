package com.carrot.market.chat.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.carrot.market.chat.presentation.dto.Message;

import lombok.Builder;
import lombok.Getter;

@Getter
@Document(collection = "chatting")
public class Chatting {
	@Id
	private String id;
	@Indexed
	private Long chatRoomId;
	private Long senderId;
	private String content;
	@Indexed
	private LocalDateTime createdAt;
	@Indexed
	private boolean isRead;

	@Builder
	public Chatting(Long chatRoomId, Long senderId, String content, boolean isRead) {
		this.chatRoomId = chatRoomId;
		this.senderId = senderId;
		this.content = content;
		this.createdAt = LocalDateTime.now();
		this.isRead = isRead;
	}

	public static Chatting from(Message message) {
		return Chatting.builder()
			.chatRoomId(message.getChatroomId())
			.content(message.getContent())
			.senderId(message.getSenderId())
			.isRead(message.isRead())
			.build();
	}

	public void readChatting() {
		isRead = true;
	}
}
