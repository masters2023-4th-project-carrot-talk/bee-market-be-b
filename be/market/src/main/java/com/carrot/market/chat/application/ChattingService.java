package com.carrot.market.chat.application;

import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.infrastructure.mongo.ChattingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChattingService {
    private final ChattingRepository chatRepository;

    public void saveAll(List<Chatting> chattings) {
        chatRepository.saveAll(chattings);
    }
	
}
