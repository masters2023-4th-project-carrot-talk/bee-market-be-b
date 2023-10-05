package com.carrot.market.chatroom.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;
import com.carrot.market.chatroom.application.dto.response.ChattingroomListResponse;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

import jakarta.persistence.EntityManager;

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
	ChattingRepository chattingRepository;
	@Autowired
	ChatroomCounterRepository chatRoomCounterRepository;
	@Autowired
	LocationRepository locationRepository;
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	ProductImageRepository productImageRepository;
	@Autowired
	EntityManager entityManager;

	@AfterEach
	void tearDown() {
		chattingRepository.deleteAll();
		chatRoomCounterRepository.deleteAll();
	}

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
		chatroomService.connectChatRoom(chatroom.getId(), "sessionId");

		// then
		List<ChatroomCounter> chatroomCounters = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
		assertThat(chatroomCounters).hasSize(1)
			.extracting("chatroomId", "sessionId")
			.containsExactly(tuple(chatroom.getId(), "sessionId"));
	}

	@Test
	void disconnectChatRoom() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));
		ChatroomCounter chatRoomCounter = ChatroomCounter.builder()
			.sessionId("sessionId")
			.chatroomId(chatroom.getId())
			.build();
		chatRoomCounterRepository.save(chatRoomCounter);
		// when
		chatroomService.disconnectChatRoom("sessionId");

		// then
		List<ChatroomCounter> byChatroomId = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
		assertThat(byChatroomId).hasSize(0);
	}

	@Test
	void getChatDetailsWithSingleChatroom() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Product product = productRepository.save(Product.builder().seller(seller).build());
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));

		Chatting firstChat = new Chatting(chatroom.getId(), seller.getId(), "hello", false);
		chattingRepository.save(firstChat);

		// when
		for (int index = 0; index < 10; index++) {
			Chatting chatting = new Chatting(chatroom.getId(), seller.getId(), "hello" + index, false);
			chatting.readChatting();
			chattingRepository.save(chatting);
		}

		// then
		List<ChatroomInfo> chatDetails = chattingRepository.getChatDetails(List.of(chatroom.getId()),
			purchaser.getId());
		assertThat(chatDetails).hasSize(1)
			.extracting("unreadChatCount", "chatRoomId", "lastChatContent")
			.containsExactly(tuple(1L, chatroom.getId(), "hello9"));
	}

	@Test
	void getChatDetailsWithMultiChatroom() throws InterruptedException {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Member purchaser2 = memberRepository.save(makeMember("sully", "www.google.com"));

		Product product = productRepository.save(Product.builder().seller(seller).build());

		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));
		Chatroom chatroom2 = chatroomRepository.save(new Chatroom(product, purchaser2));

		Chatting firstChat = new Chatting(chatroom.getId(), seller.getId(), "hello", false);
		Chatting secondChat = new Chatting(chatroom2.getId(), purchaser.getId(), "hello2", false);
		Thread.sleep(3000);
		Chatting thirdChat = new Chatting(chatroom2.getId(), seller.getId(), "hello3", false);
		chattingRepository.saveAll(List.of(firstChat, secondChat));
		chattingRepository.save(thirdChat);

		// when
		List<ChatroomInfo> chatDetails = chattingRepository.getChatDetails(
			List.of(chatroom.getId(), chatroom2.getId()), seller.getId());

		// then
		assertThat(chatDetails).hasSize(2)
			.extracting("unreadChatCount", "chatRoomId", "lastChatContent")
			.contains(tuple(0L, chatroom.getId(), "hello"), tuple(1L, chatroom2.getId(), "hello3"));
	}

	@Test
	void getChattingList() {
		// given
		Member seller = memberRepository.save(makeMember("June", "www.naver.com"));
		Member purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		Member purchaser2 = memberRepository.save(makeMember("sully", "www.google.com"));
		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.naver.com");
		imageRepository.save(image);
		Product product = productRepository.save(Product.builder().location(location).seller(seller).build());
		ProductImage productImage = makeProductImage(product, image, true);
		productImageRepository.save(productImage);
		Chatroom chatroom = chatroomRepository.save(new Chatroom(product, purchaser));
		Chatroom chatroom2 = chatroomRepository.save(new Chatroom(product, purchaser2));

		Chatting firstChat = new Chatting(chatroom.getId(), purchaser.getId(), "hello", false);
		Chatting secondChat = new Chatting(chatroom2.getId(), purchaser2.getId(), "hello2", false);
		Chatting thirdChat = new Chatting(chatroom2.getId(), purchaser2.getId(), "hello3", false);
		chattingRepository.saveAll(List.of(firstChat, secondChat, thirdChat));

		// when
		List<ChattingroomListResponse> chattingList = chatroomService.getChattingroomList(seller.getId());

		// then
		assertThat(chattingList).hasSize(2)
			.extracting("nickname", "imageUrl", "locationName", "productMainImage", "unreadChatCount",
				"lastChatContent")
			.contains(tuple("bean", "www.google.com", "susongdong", "www.naver.com", 1L, "hello"),
				tuple("sully", "www.google.com", "susongdong", "www.naver.com", 2L, "hello3"));

		assertAll(
			() -> assertThat(chattingList.get(0).lastChatTime().truncatedTo(ChronoUnit.SECONDS))
				.isEqualTo(firstChat.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)),
			() -> assertThat(chattingList.get(1).lastChatTime().truncatedTo(ChronoUnit.SECONDS))
				.isEqualTo(thirdChat.getCreatedAt().truncatedTo(ChronoUnit.SECONDS)
				));
	}
}