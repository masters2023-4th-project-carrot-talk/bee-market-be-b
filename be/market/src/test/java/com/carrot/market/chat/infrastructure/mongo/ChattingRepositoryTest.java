package com.carrot.market.chat.infrastructure.mongo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.support.IntegrationTestSupport;

class ChattingRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ChattingRepository chattingRepository;
	@Autowired
	ChatroomRepository chatroomRepository;

	@Test
	void findByChatRoomIdWithFirstPage() {
		// given
		Chatroom chatroom = chatroomRepository.save(Chatroom.builder().product(null).purchaser(null).build());
		for (int num = 0; num < 10; num++) {
			Chatting chatting = Chatting.builder()
				.chatRoomId(chatroom.getId())
				.content("hello" + num)
				.senderId(1L)
				.build();
			chattingRepository.save(chatting);
		}
		// then
		List<Chatting> byChatRoomIdWithPageable = chattingRepository.findByChatRoomIdWithPageable(chatroom.getId(),
			LocalDateTime.now(), 5);
		// when
		assertThat(byChatRoomIdWithPageable).hasSize(5);

	}

	@Test
	void findByChatRoomIdWithPageable() {
		// given
		Chatroom chatroom = chatroomRepository.save(Chatroom.builder().product(null).purchaser(null).build());
		for (int num = 0; num < 10; num++) {
			Chatting chatting = Chatting.builder()
				.chatRoomId(chatroom.getId())
				.content("hello" + num)
				.senderId(1L)
				.build();
			chattingRepository.save(chatting);
		}
		// then
		List<Chatting> byChatRoomIdWithPageable = chattingRepository.findByChatRoomIdWithPageable(chatroom.getId(),
			LocalDateTime.now(), 5);
		byChatRoomIdWithPageable.forEach(chatting -> System.out.println(chatting.getContent()));

		// when
		Chatting lastChatting = byChatRoomIdWithPageable.get(byChatRoomIdWithPageable.size() - 1);
		List<Chatting> byChatRoomIdWithPageable2 = chattingRepository.findByChatRoomIdWithPageable(chatroom.getId(),
			lastChatting.getCreatedAt(), 3);
		assertThat(byChatRoomIdWithPageable2).hasSize(3);

	}

}