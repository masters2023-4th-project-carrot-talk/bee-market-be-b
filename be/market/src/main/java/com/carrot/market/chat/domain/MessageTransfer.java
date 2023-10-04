package com.carrot.market.chat.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.carrot.market.chat.presentation.dto.Message;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;

@Getter
public class MessageTransfer implements Serializable {
	private Long senderId;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
	private List<MessageTransferPrepared> pendingEvents = new ArrayList<>();

	public void addPendingChatting(Message message) {
		this.pendingEvents.add(new MessageTransferPrepared(message));
	}

	public static MessageTransfer prepareMessageTransfer(Message message) {
		MessageTransfer chattingTransfer = new MessageTransfer();
		chattingTransfer.pendAndApplyEvent(
			new MessageTransferPrepared(message),
			chattingTransfer::apply
		);
		return chattingTransfer;
	}

	private void pendAndApplyEvent(MessageTransferPrepared event, Consumer<MessageTransferPrepared> apply) {
		this.pendingEvents.add(event);
		apply.accept(event);
	}

	private void apply(MessageTransferPrepared messageTransferPrepared) {
		this.senderId = messageTransferPrepared.getMessage().getSenderId();
	}

	public void clearMessages() {
		pendingEvents = new ArrayList<>();

	}
}
