package com.carrot.market.chatroom.application.dto.response;

import java.util.List;

public record ChattingResponse(
	List<ChattingListResponse> chattings,
	String nextId
) {
}
