package com.carrot.market.product.domain;

import java.util.Arrays;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ProductException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellingStatus {
	SELLING("판매중"),
	RESERVED("예약중"),
	SOLD_OUT("판매완료");

	private final String text;

	public static SellingStatus from(String text) {
		return Arrays.stream(SellingStatus.values())
			.filter(status -> status.text.equals(text))
			.findFirst()
			.orElseThrow(() -> new ApiException(ProductException.INVALIED_PRODUCT_STATUS));
	}
}
