package com.carrot.market.chat.presentation;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.chat.application.ChatService;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.presentation.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@MessageMapping("/message")
	public ApiResponse<Void> sendMessage(Message message) {
		chatService.sendMessage(message);
		return ApiResponse.successNoBody();
	}
}
