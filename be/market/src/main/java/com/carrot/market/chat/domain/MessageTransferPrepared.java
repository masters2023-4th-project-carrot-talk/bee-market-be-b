package com.carrot.market.chat.domain;

import com.carrot.market.chat.presentation.dto.Message;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageTransferPrepared {
	private Message message;
}
