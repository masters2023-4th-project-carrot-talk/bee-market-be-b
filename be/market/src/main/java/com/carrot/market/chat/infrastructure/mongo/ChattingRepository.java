package com.carrot.market.chat.infrastructure.mongo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chatroom.application.dto.response.ChatroomInfo;

@Repository
public interface ChattingRepository extends MongoRepository<Chatting, String> {
	@Aggregation(pipeline = {
		"{ '$match': { 'chatRoomId' : ?0  ,  'createdAt' : { $lt : ?1 } } }",
		"{ '$sort' : { 'createdAt' : -1 } }",
		"{ '$limit' : ?2 }"
	})
	List<Chatting> findByChatRoomIdWithPageable(Long chatRoomId, LocalDateTime chattingTime, int limit);

	@Aggregation(pipeline = {
		"{ '$match': { 'chatRoomId' : {'$in' : ?0}  ,  'readCount' : 1  } }",
		"{ '$sort' : { 'createdAt' : -1 } }",
		"{ '$group' : { '_id' : '$chatRoomId' , "
			+ "'latestChatContent' : { '$last' : '$content'}, "
			+ "'chatRoomId' : { '$last' : '$chatRoomId'}, "
			+ "'createdAt' : { '$last' : '$createdAt'}, "
			+ "'unreadChatCount' : { '$sum' : 1}}} "
	})
	List<ChatroomInfo> getChatDetails(List<Long> chatroomIds);
}