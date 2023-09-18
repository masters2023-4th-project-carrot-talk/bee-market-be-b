package com.carrot.market.chat.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {
	private final SimpMessagingTemplate template;
	private final String DESTINATION = "/subscribe/";

	@KafkaListener(
		topics = KafkaConstant.KAFKA_TOPIC,
		groupId = KafkaConstant.GROUP_ID
	)
	public void listen(Message message) {
		log.debug("sending via kafka listener.." + message.toString());
		template.convertAndSend(DESTINATION + message.getChatroomId(), message);
	}
}
