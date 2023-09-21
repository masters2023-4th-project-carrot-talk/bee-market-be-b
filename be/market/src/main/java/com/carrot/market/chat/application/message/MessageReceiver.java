package com.carrot.market.chat.application.message;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
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

	@RetryableTopic(
		dltStrategy = DltStrategy.FAIL_ON_ERROR,
		listenerContainerFactory = "retryConcurrentFactory",
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		kafkaTemplate = "messageKafkaTemplate")
	@KafkaListener(
		topics = KafkaConstant.KAFKA_TOPIC,
		groupId = KafkaConstant.GROUP_ID,
		containerFactory = "kafkaMessageListenerContainerFactory"
	)
	public void listen(Message message) {
		log.debug("sending via kafka listener.." + message.toString());
		template.convertAndSend(DESTINATION + message.getChatroomId(), message);
	}

	/**
	 * Dlt에 메세지 쌓일 때 실패 로그 쌓음
	 */
	@DltHandler
	public void dltHandler(ConsumerRecord<String, String> record,
		@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
		@Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
		@Header(KafkaHeaders.OFFSET) Long offset,
		@Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
		log.error("received message='{}' with partitionId='{}', offset='{}', topic='{}'", record.value(), offset,
			partitionId, topic);
		// kafkaConsumerService.saveFailedMessage(topic, partitionId, offset, record.value(), errorMessage);
	}
}
