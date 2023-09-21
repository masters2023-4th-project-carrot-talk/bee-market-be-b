package com.carrot.market.chat.application.entry;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.carrot.market.chat.presentation.dto.Entry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntrySender {
	private final KafkaTemplate<String, Entry> entryKafkaTemplate;

	public void send(String topic, Entry entry) {
		CompletableFuture<SendResult<String, Entry>> send = entryKafkaTemplate.send(topic, entry);
		send.whenComplete(successCallback(entry));
	}

	private BiConsumer<SendResult<String, Entry>, Throwable> successCallback(Entry data) {
		return (result, ex) -> {
			if (sendMessageSuccess(result)) {
				log.info("anyone enter room " + data.isAnyoneEnterRoom() + ", " + data.getChatroomId());
			}
		};
	}

	private boolean sendMessageSuccess(SendResult<String, Entry> result) {
		return result != null;
	}
}
