package com.carrot.market.chatroom.application.dto.response;

public record ChattingAdditionalInfoResponse(
	ChattingOpponentResponse opponent,
	ChattingProductResponse product
) {

	public static ChattingAdditionalInfoResponse from(ChattingOpponentResponse chattingOpponentResponse,
		ChattingProductResponse chattingProductResponse) {
		return new ChattingAdditionalInfoResponse(chattingOpponentResponse, chattingProductResponse);
	}
}
