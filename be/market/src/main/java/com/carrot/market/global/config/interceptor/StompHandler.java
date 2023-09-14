package com.carrot.market.global.config.interceptor;

import static com.carrot.market.global.filter.JwtAuthorizationFilter.*;

import java.util.Objects;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import com.carrot.market.chat.application.ChatService;
import com.carrot.market.chatroom.application.ChatroomService;
import com.carrot.market.global.exception.domain.JwtException;
import com.carrot.market.jwt.application.JwtProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final JwtProvider jwtProvider;
	private final ChatroomService chatroomService;
	private final ChatService chatService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		// StompCommand에 따라서 로직을 분기해서 처리하는 메서드를 호출한다.
		log.info("StompAccessor = {}", accessor);
		handleMessage(accessor.getCommand(), accessor);
		return message;
	}

	private void handleMessage(StompCommand stompCommand, StompHeaderAccessor accessor) {
		switch (stompCommand) {

			case CONNECT:
				log.info("CONNECT !!");
				Long connectSenderId = validateToken(accessor);
				connectToChatRoom(accessor, connectSenderId);
				break;
			case SUBSCRIBE:
				log.info("SUBSCRIBE !!");
				break;
			case SEND:
				break;
			case DISCONNECT:
				log.info("DISCONNECT !!");
				Long disconnectSenderId = validateToken(accessor);
				disconnectChatRoom(accessor, disconnectSenderId);
				break;
		}
	}

	private void connectToChatRoom(StompHeaderAccessor accessor, Long senderId) {
		Long chatRoomId = getChatRoomId(accessor);
		chatroomService.connectChatRoom(chatRoomId, senderId);
		chatService.readChattingInChatroom(chatRoomId);
	}

	private void disconnectChatRoom(StompHeaderAccessor accessor, Long senderId) {
		Long chatRoomId = getChatRoomId(accessor);
		chatroomService.disconnectChatRoom(chatRoomId, senderId);
	}

	private Long getChatRoomId(StompHeaderAccessor accessor) {
		return
			Long.valueOf(
				Objects.requireNonNull(
					accessor.getFirstNativeHeader("chatRoomId")
				));
	}

	private String getAccessToken(StompHeaderAccessor accessor) {
		return accessor.getFirstNativeHeader("Authorization");
	}

	private Long validateToken(StompHeaderAccessor accessor) {
		try {
			String token = getAccessToken(accessor);
			Claims claims = jwtProvider.getClaims(token);
			return Long.valueOf((Integer)claims.get(MEMBER_ID));
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
			IllegalArgumentException ex) {
			throw new IllegalStateException(JwtException.from(ex).getMessage());
		}
	}

}
