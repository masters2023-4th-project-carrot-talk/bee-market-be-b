package com.carrot.market.notification.domain;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.NotificationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotificationEmitters {

	public static final String SSE_SUBSCRIBE_NAME = "connect";
	public static final String SSE_EVENT_NAME = "notification";
	private static final String INIT_CONNECTED_MESSAGE = "connected";

	private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

	@Value("${sse.timeout:600}")
	private long timeout;

	public SseEmitter add(Long key) {
		final SseEmitter sseEmitter = new SseEmitter(timeout);
		sseEmitters.put(key, sseEmitter);
		send(key, SSE_SUBSCRIBE_NAME, INIT_CONNECTED_MESSAGE);

		return sseEmitter;
	}

	public void send(Long key, String name, String data) {
		final SseEmitter sseEmitter = sseEmitters.get(key);
		if (sseEmitter == null) {
			return;
		}

		try {
			sseEmitter.send(SseEmitter.event()
				.id(String.valueOf(key))
				.name(name)
				.data(data));
		} catch (IOException ex) {
			sseEmitter.completeWithError(new ApiException(NotificationException.NOTIFICATION_SEND_FAILED));
		}
	}

	public void send(Long key, Notification notification) {
		send(key, SSE_EVENT_NAME, notification.toString());
	}
}
