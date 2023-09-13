package com.carrot.market.chat.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.carrot.market.chat.domain.Chatting;

public interface ChatRepository extends MongoRepository<Chatting, String> {

}
