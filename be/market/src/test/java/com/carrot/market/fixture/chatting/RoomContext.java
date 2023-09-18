package com.carrot.market.fixture.chatting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.carrot.market.chat.presentation.dto.Message;

public class RoomContext {

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
