package com.carrot.market.chat.application;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

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
	private final SimpMessagingTemplate template;
	private final MemberRepository memberRepository;
	private final ChatroomRepository chatroomRepository;
	private final NotificationService notificationService;
	private static final String DESTINATION = "/subscribe/";

	@KafkaListener(
		topics = KafkaConstant.KAFKA_TOPIC,
		groupId = KafkaConstant.GROUP_ID
	)
	public void listen(Message message) {
		log.debug("sending via kafka listener.." + message.toString());
		template.convertAndSend(DESTINATION + message.getChatroomId(), message);

		try {
			sendNotification(message);
		} catch (ApiException ex) {
			log.debug("[notification failed] " + ex.getMessage());
		}
	}

	private void sendNotification(Message message) {
		Chatroom chatroom = chatroomRepository.findChatroomFetchById(message.getChatroomId())
			.orElseThrow(() -> new ApiException(ChattingException.NOT_FOUND_CHATROOM));
		Member sender = memberRepository.findById(message.getSenderId())
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		Member receiver = chatroom.getReceiver(sender);
		var notification = Notification.create(chatroom.getProductTitle(), sender.getNickname(), message.getContent());

		notificationService.send(receiver, notification);
	}
}
