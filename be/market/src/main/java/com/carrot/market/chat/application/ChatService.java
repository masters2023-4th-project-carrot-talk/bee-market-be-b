package com.carrot.market.chat.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChatRepository;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {
	private final ChatRepository chatRepository;
	private final MessageSender messageSender;

	@Transactional
	public void sendMessage(Message message) {
		Chatting chatting = Chatting.from(message);
		chatRepository.save(chatting);
		messageSender.send(KafkaConstant.KAFKA_TOPIC, message);
	}
}
