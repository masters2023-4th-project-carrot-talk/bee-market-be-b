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

	private String title;

	private Long price;

	private String content;

	private Long hits = 0L;

	@Builder
	public ProductDetails(String title, Long price, String content, Long hits) {
		this.title = title;
		this.price = price;
		this.content = content;
		this.hits = hits;
	}
}
