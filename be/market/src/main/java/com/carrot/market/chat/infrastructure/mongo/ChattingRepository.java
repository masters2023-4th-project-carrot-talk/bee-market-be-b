package com.carrot.market.chat.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.carrot.market.chat.domain.Chatting;

public interface ChattingRepository extends MongoRepository<Chatting, String> {

}
