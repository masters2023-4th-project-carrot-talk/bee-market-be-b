package com.carrot.market.chatroom.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.chatroom.application.ChatroomService;
import com.carrot.market.chatroom.application.dto.response.ChattingAdditionalInfoResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingResponse;
import com.carrot.market.chatroom.application.dto.response.ChattingroomListResponse;
import com.carrot.market.chatroom.application.dto.response.UnreadChatTotalCountResponse;
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

	@GetMapping("/chatrooms")
	public ApiResponse<List<ChattingroomListResponse>> getChatroom(
		@Login MemberId memberId
	) {
		List<ChattingroomListResponse> chattingroomList = chatroomService.getChattingroomList(memberId.getMemberID());
		return ApiResponse.success(chattingroomList);
	}

	@GetMapping("/chatrooms/{chatroomId}")
	public ApiResponse<ChattingResponse> getChatting(
		@PathVariable Long chatroomId,
		@RequestParam(required = false) String next
	) {
		ChattingResponse chattingList = chatroomService.getChattingList(chatroomId, next);
		return ApiResponse.success(chattingList);
	}

	@GetMapping("/chatrooms/{chatroomId}/product")
	public ApiResponse<ChattingAdditionalInfoResponse> getChattingAdditionalInfo(
		@PathVariable Long chatroomId,
		@Login MemberId memberId
	) {
		ChattingAdditionalInfoResponse chattingAdditionalInfo = chatroomService.getChattingAdditionalInfo(chatroomId,
			memberId.getMemberID());
		return ApiResponse.success(chattingAdditionalInfo);
	}

	@GetMapping("/chatrooms/unread-total-count")
	public ApiResponse<UnreadChatTotalCountResponse> getUnreadChatTotalCountInChatrooms(
		@Login MemberId memberId
	) {
		return ApiResponse.success(chatroomService.getUnreadChatTotalCountInChatrooms(memberId.getMemberID()));
	}
}
