package com.carrot.market.chat.infrastructure;

import com.carrot.market.chat.application.ChattingService;
import com.carrot.market.chat.application.message.MessageSender;
import com.carrot.market.chat.domain.Chatting;
import com.carrot.market.chat.domain.MessageTransfer;
import com.carrot.market.chat.domain.MessageTransferPrepared;
import com.carrot.market.global.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ChattingTransferRepository {

    private final RedisTemplate<String, MessageTransfer> redisOperations;
    private final TransactionTemplate kafkaTransactionTemplate;
    private final MessageSender messageSender;

    private final ChattingService chatService;

    @Transactional
    public void save(MessageTransfer messageTransfer) {
        List<MessageTransferPrepared> pendingEvents = messageTransfer.getPendingEvents();
        String chattingCacheKey = CacheNames.createChattingCacheKey(messageTransfer.getSenderId());
        this.redisOperations.opsForValue().set(chattingCacheKey, messageTransfer,
                Duration.ofMillis(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));

        kafkaTransactionTemplate.executeWithoutResult(transactionStatus -> {
            if (!pendingEvents.isEmpty()) {
                this.messageSender.sendAll(pendingEvents);
            }

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {

                    chatService.saveAll(messageTransfer.getPendingEvents()
                            .stream()
                            .map(messageTransferPrepared -> Chatting.from(messageTransferPrepared.getMessage()))
                            .collect(
                                    Collectors.toList()));
                    ;
                    redisOperations.delete(chattingCacheKey);
                }
            });

        });
    }

}
