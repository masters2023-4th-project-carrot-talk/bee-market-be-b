package com.carrot.market.product.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellingStatus {
	SELLING("판매중"),
	RESERVED("예약중"),
	SOLD_OUT("판매완료");

	private final String text;

	public static String fromString(String text) {
		if (text == null) {
			return null;
		}
		return Arrays.stream(SellingStatus.values())
			.filter(status -> status.text.equalsIgnoreCase(text))
			.findFirst()
			.map(Enum::name)
			.orElseThrow(() -> new IllegalArgumentException("No constant with text " + text + " found"));
	}
}
