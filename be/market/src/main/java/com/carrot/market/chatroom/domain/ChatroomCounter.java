package com.carrot.market.chatroom.domain;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "chatRoomCounter")
public class ChatroomCounter {
	@Id
	private String id;

	@Indexed
	private Long chatroomId;

	@Indexed
	private String sessionId;

	@Builder
	public ChatroomCounter(Long chatroomId, String sessionId) {
		this.chatroomId = chatroomId;
		this.sessionId = sessionId;
	}
}