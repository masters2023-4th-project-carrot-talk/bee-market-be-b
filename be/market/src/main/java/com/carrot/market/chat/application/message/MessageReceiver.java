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

import com.carrot.market.chat.application.ChatService;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ChattingException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.global.util.KafkaConstant;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.notification.application.NotificationService;
import com.carrot.market.notification.domain.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReceiver {
	private static final String DESTINATION = "/subscribe/";

	private final SimpMessagingTemplate template;
	private final MemberRepository memberRepository;
	private final ChatroomRepository chatroomRepository;
	private final NotificationService notificationService;
	private final ChatService chatService;

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
		log.info("sending via kafka listener.." + message.toString());
		template.convertAndSend(DESTINATION + message.getChatroomId(), message);

		if (chatService.isAnyoneInChatroom(message.getChatroomId())) {
			return;
		}
		try {
			sendNotification(message);
		} catch (Exception ex) {

			log.info("[notification failed] " + ex.getMessage());
		}
	}

	private void sendNotification(Message message) {
		Chatroom chatroom = chatroomRepository.findChatroomFetchById(message.getChatroomId())
			.orElseThrow(() -> new ApiException(ChattingException.NOT_FOUND_CHATROOM));
		Member sender = memberRepository.findById(message.getSenderId())
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		Member receiver = chatroom.getReceiver(sender);
		var notification = Notification.create(chatroom.getId(), chatroom.getProductTitle(), sender.getNickname(),
			message.getContent());

		notificationService.send(receiver, notification);
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
