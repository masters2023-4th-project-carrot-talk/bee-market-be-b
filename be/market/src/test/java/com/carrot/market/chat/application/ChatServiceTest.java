package com.carrot.market.chat.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static com.carrot.market.global.filter.JwtAuthorizationFilter.*;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

class ChatServiceTest extends IntegrationTestSupport {
	private static final String SUBSCRIBE_ROOM_UPDATE_BROAD_ENDPOINT_FORMAT = "/subscribe/%s";
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ChatroomRepository chatroomRepository;
	@Autowired
	private ChatService chatService;
	@Autowired
	private JwtProvider jwtProvider;

	private BlockingQueue<Message> blockingQueueForChatting;
	private RoomContext roomContext;

	private Member seller;
	private Member purchaser;
	private Product product;
	private Chatroom chatroom;
	private String accessToken;

	@BeforeEach
	void before() {
		blockingQueueForChatting = new LinkedBlockingDeque<>();
		roomContext = new RoomContext(blockingQueueForChatting, port);

		seller = memberRepository.save(makeMember("June", "www.naver.com"));
		purchaser = memberRepository.save(makeMember("bean", "www.google.com"));
		product = productRepository.save(Product.builder().seller(seller).build());
		chatroom = chatroomRepository.save(new Chatroom(product, seller));
		accessToken = jwtProvider.createAccessToken(Map.of(MEMBER_ID, seller.getId()));
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
		enter_room(chatroom.getId(), accessToken, roomContext);

		sendMessage(purchaser.getId(), chatroom.getId(), content);
		Message message = blockingQueueForChatting.poll(30, SECONDS);

		//then
		assertThat(message).usingRecursiveComparison().isEqualTo(expectedMessageResponse);

	}

	private void sendMessage(Long senderId, Long roomId, String content) {
		Message message = Message.builder().senderId(senderId).chatroomId(roomId).content(content).build();
		chatService.sendMessage(message);
	}

	private static StompSession enter_room(Long chatroomId, String accessToken, RoomContext roomContext) throws
		ExecutionException,
		InterruptedException,
		TimeoutException {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", accessToken);

		StompSession stompSession = stompClient.connectAsync(
				String.format("ws://localhost:%d/chat", roomContext.getPort()), new WebSocketHttpHeaders(), stompHeaders,
				new StompSessionHandlerAdapter() {
				})
			.get(20, SECONDS);

		stompSession.subscribe(String.format(SUBSCRIBE_ROOM_UPDATE_BROAD_ENDPOINT_FORMAT, chatroomId),
			new ChatUpdateStompFrameHandler(roomContext.getBlockingQueueForMessage()));

		return stompSession;
	}

	private static class ChatUpdateStompFrameHandler implements StompFrameHandler {

		private final BlockingQueue<Message> blockingQueue;

		public ChatUpdateStompFrameHandler(final BlockingQueue<Message> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return Message.class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			blockingQueue.offer((Message)o);
		}
	}

	private static class RoomContext {

		private final BlockingQueue<Message> blockingQueueForMessage;
		private final int port;

		public RoomContext(final int port) {
			this(new LinkedBlockingDeque<>(), port);
		}

		public RoomContext(BlockingQueue<Message> blockingQueueForMessage, int port) {
			this.blockingQueueForMessage = blockingQueueForMessage;
			this.port = port;
		}

		public BlockingQueue<Message> getBlockingQueueForMessage() {
			return blockingQueueForMessage;
		}

		public int getPort() {
			return port;
		}
	}
}