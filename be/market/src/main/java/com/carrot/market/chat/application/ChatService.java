package com.carrot.market.chat.application;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.carrot.market.chat.application.entry.EntrySender;
import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.domain.MessageTransfer;
import com.carrot.market.chat.infrastructure.ChattingTransferRepository;
import com.carrot.market.chat.presentation.dto.Entry;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.chatroom.infrastructure.redis.ChatroomCounterRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {
	private final EntrySender entrySender;
	private final ChatroomCounterRepository chatroomCounterRepository;
	private final ChattingTransferRepository chattingTransferRepository;
	private final MongoTemplate mongoTemplate;

	public void sendMessage(Message message) {
		if (isAnyoneInChatroom(message.getChatroomId())) {
			message.readMessage();
		}
		chattingTransferRepository.save(MessageTransfer.prepareMessageTransfer(message));
	}

	public void sendEntry(Entry entry) {
		entrySender.send("bee-chat2", entry);
	}

	public boolean isAnyoneInChatroom(Long chatroomId) {
		return chatroomCounterRepository.findByChatroomId(chatroomId).size() == 2;
	}

	public void readChattingInChatroom(Long chatRoomId, Long memberId) {

		Update update = new Update().set("isRead", true);
		Query query = new Query();
		query.addCriteria(Criteria.where("chatRoomId").is(chatRoomId));
		query.addCriteria(Criteria.where("senderId").ne(memberId));

		mongoTemplate.updateMulti(query, update, Chatting.class);
	}

}
