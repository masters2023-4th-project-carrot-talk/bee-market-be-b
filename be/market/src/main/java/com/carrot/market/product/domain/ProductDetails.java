package com.carrot.market.product.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ProductDetails {

	private String name;

	private Long price;

	private String content;

	private Long hits = 0L;

	@Builder
	public ProductDetails(String name, Long price, String content, Long hits) {
		this.name = name;
		this.price = price;
		this.content = content;
		this.hits = hits;
	}
}
