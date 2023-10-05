package com.carrot.market.chat.infrastructure.mongo;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;
import com.carrot.market.chatroom.application.dto.response.UnreadChatTotalCountResponse;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.support.IntegrationTestSupport;

class ChattingRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ChattingRepository chattingRepository;
	@Autowired
	ChatroomRepository chatroomRepository;
	@Autowired
	MemberRepository memberRepository;
	private Chatroom chatroom;
	private Member june;
	private Member bean;

	@BeforeEach
	void before() {
		june = makeMember("june", "123");
		bean = makeMember("bean", "1234");
		memberRepository.saveAll(List.of(june, bean));
		Product product = makeProduct(june, null, null, null, null);
		chatroom = chatroomRepository.save(Chatroom.builder().product(product).purchaser(june).build());
		for (int num = 0; num < 10; num++) {
			Chatting chatting = Chatting.builder()
				.chatRoomId(chatroom.getId())
				.content("hello" + num)
				.senderId(1L)
				.isRead(true)
				.build();
			chattingRepository.save(chatting);
		}
	}

	@AfterEach
	void after() {
		chattingRepository.deleteAll();
	}

	@Test
	void getUnreadChatTotalCount() {
		Chatting hello = new Chatting(chatroom.getId(), june.getId(), "hello", false);
		chattingRepository.save(hello);

		UnreadChatTotalCountResponse unreadChatTotalCount = chattingRepository.getUnreadChatTotalCount(
			List.of(chatroom.getId()), bean.getId());

		assertThat(unreadChatTotalCount.unreadTotalCount()).isEqualTo(1);
	}

	@Test
	void getChatDetails() {
		// given & when
		Chatroom chatroom1 = chatroomRepository.save(Chatroom.builder().product(null).purchaser(null).build());
		List<ChatroomInfo> chatDetails = chattingRepository.getChatDetails(
			List.of(chatroom.getId(), chatroom1.getId(), 3L), null);

		// then
		assertThat(chatDetails.size()).isEqualTo(1);
		assertThat(chatDetails.get(0).chatRoomId()).isEqualTo(chatroom.getId());
		assertThat(chatDetails.get(0).unreadChatCount()).isEqualTo(0);
		assertThat(chatDetails.get(0).lastChatContent()).isEqualTo("hello9");

	}

	@Test
	void findByChatRoomIdWithFirstPage() {
		// given

		// then
		List<Chatting> byChatRoomIdWithPageable = chattingRepository.findRecentChatsByChatRoomId(chatroom.getId(),
			LocalDateTime.now(), 5);
		// when
		assertThat(byChatRoomIdWithPageable).hasSize(5);

	}

	@Test
	void findByChatRoomIdWithPageable() {
		// given
		List<Chatting> byChatRoomIdWithPageable = chattingRepository.findRecentChatsByChatRoomId(chatroom.getId(),
			LocalDateTime.now(), 5);

		// when
		Chatting lastChatting = byChatRoomIdWithPageable.get(byChatRoomIdWithPageable.size() - 1);
		List<Chatting> byChatRoomIdWithPageable2 = chattingRepository.findRecentChatsByChatRoomId(chatroom.getId(),
			lastChatting.getCreatedAt(), 3);
		assertThat(byChatRoomIdWithPageable2).hasSize(3);

	}

}