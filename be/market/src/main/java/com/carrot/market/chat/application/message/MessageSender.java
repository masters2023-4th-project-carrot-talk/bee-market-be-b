package com.carrot.market.chat.application.message;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chat.presentation.dto.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender {
	private final KafkaTemplate<String, Message> messageKafkaTemplate;
	private final ChattingRepository chattingRepository;

	// 메시지를 지정한 Kafka 토픽으로 전송
	public void send(String topic, Message message) {
		log.info(message.toString());
		// KafkaTemplate을 사용하여 메시지를 지정된 토픽으로 전송
		message.setChatTime();
		CompletableFuture<SendResult<String, Message>> send = messageKafkaTemplate.send(topic, message);
		send.whenComplete(successCallback(message));
	}

	private BiConsumer<SendResult<String, Message>, Throwable> successCallback(Message data) {
		return (result, ex) -> {
			if (sendMessageSuccess(result)) {
				Chatting chatting = Chatting.from(data);
				chattingRepository.save(chatting);
			}
		};
	}

	private boolean sendMessageSuccess(SendResult<String, Message> result) {
		return result != null;
	}
}
