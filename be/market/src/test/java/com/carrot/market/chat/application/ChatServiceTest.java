package com.carrot.market.chat.application;

import static com.carrot.market.fixture.ChattingFixtureFactory.*;
import static com.carrot.market.fixture.FixtureFactory.*;
import static com.carrot.market.global.filter.JwtAuthorizationFilter.*;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chat.presentation.dto.Entry;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.chatroom.application.ChatroomService;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.domain.ChatroomCounter;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;
import com.carrot.market.fixture.chatting.RoomContext;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ChattingException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

class ChatServiceTest extends IntegrationTestSupport {
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ChatroomRepository chatroomRepository;
	@Autowired
	private ChatService chatService;
	@SpyBean
	private ChatroomService chatRoomService;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private ChattingRepository chattingRepository;
	@Autowired
	private ChatroomCounterRepository chatRoomCounterRepository;

	private BlockingQueue<Message> blockingQueueForChatting;
	private BlockingQueue<Entry> blockingQueueForEntry;

	private RoomContext roomContext;

	private Member seller;
	private Member purchaser;
	private Product product;
	private Chatroom chatroom;
	private String accessToken;

	@BeforeEach
	void before() {
		blockingQueueForChatting = new LinkedBlockingDeque<>();
		blockingQueueForEntry = new LinkedBlockingDeque<>();
		roomContext = new RoomContext(blockingQueueForChatting, blockingQueueForEntry, port);

		seller = memberRepository.save(makeMember("June", "www.naver.com"));
		purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		product = productRepository.save(Product.builder().seller(seller).build());
		chatroom = chatroomRepository.save(new Chatroom(product, seller));
		accessToken = jwtProvider.createAccessToken(Map.of(MEMBER_ID, seller.getId()));
	}

	@AfterEach
	void after() {
		chattingRepository.deleteAll();
		chatRoomCounterRepository.deleteAll();
	}

	@Test
	void readChattingInChatroom() {
		// given
		Chatting chatting = Chatting.builder()
			.chatRoomId(chatroom.getId())
			.senderId(purchaser.getId())
			.content("content")
			.build();
		Chatting savedChatting = chattingRepository.save(chatting);

		// when
		chatService.readChattingInChatroom(chatroom.getId());

		// then
		Chatting readChatting = chattingRepository.findById(savedChatting.getId()).orElseThrow(() -> new ApiException(
			ChattingException.INVALID_CHATTING_ID));
		assertAll(
			() -> assertThat(readChatting.getId()).isEqualTo(savedChatting.getId()),
			() -> assertThat(readChatting.isRead()).isEqualTo(true));
	}

	@Test
	void connectStompThenMakeChatRoom() throws ExecutionException, InterruptedException, TimeoutException {
		String content = "맥북 프로 사주세요";
		Message expectedMessageResponse = Message.builder()
			.senderId(purchaser.getId())
			.chatroomId(chatroom.getId())
			.content(content)
			.build();

		// init setting
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		// Connection
		enterRoom(chatroom.getId(), accessToken, roomContext);
		sendMessage(purchaser.getId(), chatroom.getId(), content);
		blockingQueueForChatting.poll(30, SECONDS);
		Message message = blockingQueueForChatting.poll(30, SECONDS);

		//then
		assertThat(message).usingRecursiveComparison().isEqualTo(expectedMessageResponse);

	}

	@Test
	void disconnectStomp() throws ExecutionException, InterruptedException, TimeoutException {

		// init setting
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		// Connection
		StompSession session = enterRoom(chatroom.getId(), accessToken, roomContext);
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", accessToken);
		stompHeaders.add("chatRoomId", String.valueOf(chatroom.getId()));
		session.disconnect(stompHeaders);

		//then
		await().atMost(10, TimeUnit.SECONDS)
			.untilAsserted(
				() -> verify(chatRoomService, atLeast(1)).disconnectChatRoom(anyString()));

		List<ChatroomCounter> byChatroomId = chatRoomCounterRepository.findByChatroomId(chatroom.getId());
		assertThat(byChatroomId).hasSize(0);

	}

	private void sendMessage(Long senderId, Long roomId, String content) {
		Message message = Message.builder().senderId(senderId).chatroomId(roomId).content(content).build();
		chatService.sendMessage(message);
	}

}