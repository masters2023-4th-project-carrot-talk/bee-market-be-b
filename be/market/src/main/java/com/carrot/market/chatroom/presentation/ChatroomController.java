package com.carrot.market.chatroom.presentation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.chatroom.application.ChatroomService;
import com.carrot.market.chatroom.presentation.dto.request.ChatroomRequest;
import com.carrot.market.chatroom.presentation.dto.response.ChatroomResponse;
import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.resolver.MemberId;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatroomController {
	private final ChatroomService chatroomService;

	@PostMapping("/chatrooms")
	public ApiResponse<ChatroomResponse> getChatroomId(
		@Login MemberId memberId,
		@RequestBody ChatroomRequest request
	) {
		Long chatroomId = chatroomService.getChatroomId(request.productId(), memberId.getMemberID());

		return ApiResponse.success(new ChatroomResponse(chatroomId));

	}
}
