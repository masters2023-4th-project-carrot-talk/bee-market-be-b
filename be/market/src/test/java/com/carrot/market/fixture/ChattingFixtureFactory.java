package com.carrot.market.fixture;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.carrot.market.fixture.chatting.ChatUpdateStompFrameHandler;
import com.carrot.market.fixture.chatting.RoomContext;

public class ChattingFixtureFactory {
	private static final String SUBSCRIBE_ROOM_UPDATE_BROAD_ENDPOINT_FORMAT = "/subscribe/%s";

	public static StompSession enterRoom(Long chatroomId, String accessToken, RoomContext roomContext) throws
		ExecutionException,
		InterruptedException,
		TimeoutException {
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());

		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		StompHeaders stompHeaders = new StompHeaders();
		stompHeaders.add("Authorization", accessToken);
		stompHeaders.add("ChatroomId", String.valueOf(chatroomId));
		StompSession stompSession = stompClient.connectAsync(
				String.format("ws://localhost:%d/chat", roomContext.getPort()), new WebSocketHttpHeaders(), stompHeaders,
				new StompSessionHandlerAdapter() {
				})
			.get(20, SECONDS);
		stompSession.subscribe(String.format(SUBSCRIBE_ROOM_UPDATE_BROAD_ENDPOINT_FORMAT, chatroomId),
			new ChatUpdateStompFrameHandler(roomContext.getBlockingQueueForMessage()));

		return stompSession;
	}

}
