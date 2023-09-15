package com.carrot.market.chat.application;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {
	private final ChattingRepository chatRepository;
	private final MessageSender messageSender;
	private final MongoTemplate mongoTemplate;

	@Transactional
	public void sendMessage(Message message) {
		Chatting chatting = Chatting.from(message);
		chatRepository.save(chatting);
		messageSender.send(KafkaConstant.KAFKA_TOPIC, message);
	}

	public void readChattingInChatroom(Long chatRoomId) {

		Update update = new Update().set("readCount", 0);
		Query query = new Query(
			Criteria.where("chatRoomId").is(chatRoomId));

		mongoTemplate.updateMulti(query, update, Chatting.class);
	}

}
