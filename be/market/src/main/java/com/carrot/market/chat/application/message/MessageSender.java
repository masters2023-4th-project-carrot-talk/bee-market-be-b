package com.carrot.market.chat.application.message;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.carrot.market.chat.domain.MessageTransferPrepared;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender {
	private final KafkaTemplate<String, Message> messageKafkaTemplate;

	public void sendAll(List<MessageTransferPrepared> messageTransferPrepareds) {
		messageTransferPrepareds.forEach(message -> this.send(KafkaConstant.KAFKA_TOPIC, message.getMessage()));
	}

	// 메시지를 지정한 Kafka 토픽으로 전송
	public void send(String topic, Message message) {
		log.info(message.toString());
		// KafkaTemplate을 사용하여 메시지를 지정된 토픽으로 전송
		message.setChatTime();
		messageKafkaTemplate.send(topic, message);

	}

}
