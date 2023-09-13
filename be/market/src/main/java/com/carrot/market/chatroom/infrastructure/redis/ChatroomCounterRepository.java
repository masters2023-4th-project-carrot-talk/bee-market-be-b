package com.carrot.market.chatroom.infrastructure.redis;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.carrot.market.chatroom.domain.ChatroomCounter;

public interface ChatroomCounterRepository extends CrudRepository<ChatroomCounter, String> {
	List<ChatroomCounter> findByChatroomId(Long chatRoomId);

	Optional<ChatroomCounter> findByChatroomIdAndMemberId(Long chatroomId, Long memberId);

}
