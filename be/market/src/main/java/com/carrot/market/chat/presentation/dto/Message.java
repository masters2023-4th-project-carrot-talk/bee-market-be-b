package com.carrot.market.chat.presentation.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
	private Long chatroomId;
	private String content;
	private Long senderId;
	@JsonProperty("isRead")
	private boolean isRead;
	private LocalDateTime chatTime;

	public void readMessage() {
		this.isRead = true;
	}

	public boolean getIsRead() { // 이거
		return isRead;
	}

	public void setChatTime() {
		this.chatTime = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "Message{" +
			" content='" + content + '\'' +
			" chatroomId='" + chatroomId + '\'' +
			" senderId='" + senderId + '\'' +
			" isRead='" + isRead + '\'' +
			'}';
	}
}
