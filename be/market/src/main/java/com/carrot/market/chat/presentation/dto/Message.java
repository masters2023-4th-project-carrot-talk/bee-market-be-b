package com.carrot.market.chat.presentation.dto;

import java.io.Serializable;

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

	@Override
	public String toString() {
		return "Message{" +
			" content='" + content + '\'' +
			" chatroomId='" + chatroomId + '\'' +
			" senderId='" + senderId + '\'' +
			'}';
	}
}
