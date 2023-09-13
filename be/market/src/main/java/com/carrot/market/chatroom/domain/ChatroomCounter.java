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
	private Long memberId;

	@Builder
	public ChatroomCounter(Long chatroomId, Long memberId) {
		this.chatroomId = chatroomId;
		this.memberId = memberId;
	}
}
