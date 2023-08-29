package com.carrot.market.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellingStatus {
	SELLING("판매중"),
	RESERVED("예약중"),
	SOLD_OUT("판매완료");

	private final String text;
}
