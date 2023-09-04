package com.carrot.market.product.application.dto.response;

import java.time.LocalDateTime;

import com.carrot.market.product.domain.SellingStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSellerDetaillDto {
	Long chatCount;
	Long likeCount;
	String location;
	SellingStatus status;
	String category;
	LocalDateTime createdAt;
	String content;
	Long hits;
	String name;
	Long price;
	Long sellerId;
	String sellerName;

	public ProductSellerDetaillDto(Long chatCount, Long likeCount, String location,
		SellingStatus status, String category, LocalDateTime createdAt, String content, Long hits, String name,
		Long price, Long sellerId, String sellerName) {
		this.chatCount = chatCount;
		this.likeCount = likeCount;
		this.location = location;
		this.status = status;
		this.category = category;
		this.createdAt = createdAt;
		this.content = content;
		this.hits = hits;
		this.name = name;
		this.price = price;
		this.sellerId = sellerId;
		this.sellerName = sellerName;
	}
}
