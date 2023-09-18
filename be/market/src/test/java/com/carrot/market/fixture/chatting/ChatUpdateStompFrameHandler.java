package com.carrot.market.fixture.chatting;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import com.carrot.market.chat.presentation.dto.Message;

public class ChatUpdateStompFrameHandler implements StompFrameHandler {

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
