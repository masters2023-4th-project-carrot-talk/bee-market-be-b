package com.carrot.market.chat.presentation.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entry implements Serializable {
	private boolean anyoneEnterRoom;
	private Long chatroomId;
}
