package com.carrot.market.chat.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.carrot.market.chat.domain.Chatting;

@Repository
public interface ChattingRepository extends MongoRepository<Chatting, String> {

}
