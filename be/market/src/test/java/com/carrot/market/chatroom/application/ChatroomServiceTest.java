package com.carrot.market.chatroom.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

class ChatroomServiceTest extends IntegrationTestSupport {
	@Autowired
	ChatroomService chatroomService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ChatroomRepository chatroomRepository;
	@Autowired
	ChatroomCounterRepository chatRoomCounterRepository;

	@Test
	void getChatroomId() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));
		// when
		Long chatroomId = chatroomService.getChatroomId(product.getId(), purchaser.getId());

		// then
		assertThat(chatroomId).isEqualTo(chatroom.getId());
	}

	@Test
	void getChatroomIdIfNonExistMakeChatroom() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());

		// when
		Long chatroomId = chatroomService.getChatroomId(product.getId(), purchaser.getId());

		// then
		Chatroom byProductIdAndPurchaserId = chatroomRepository.findByProductIdAndPurchaserId(product.getId(),
			purchaser.getId()).get();
		assertThat(chatroomId).isEqualTo(byProductIdAndPurchaserId.getId());
	}

	@Test
	void connectChatRoom() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));

		// when
		chatroomService.connectChatRoom(chatroom.getId(), purchaser.getId());

		// then
		List<ChatroomCounter> chatroomCounters = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
		assertThat(chatroomCounters).hasSize(1)
			.extracting("chatroomId", "memberId")
			.containsExactly(tuple(chatroom.getId(), purchaser.getId()));
	}

	@Test
	void disconnectChatRoom() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));
		ChatroomCounter chatRoomCounter = ChatroomCounter.builder()
			.memberId(purchaser.getId())
			.chatroomId(chatroom.getId())
			.build();
		chatRoomCounterRepository.save(chatRoomCounter);
		// when
		chatroomService.disconnectChatRoom(chatroom.getId(), purchaser.getId());

		// then
		List<ChatroomCounter> byChatroomId = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
		assertThat(byChatroomId).hasSize(0);
	}
}