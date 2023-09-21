package com.carrot.market.product.application.dto.response;

import java.time.LocalDateTime;

import com.carrot.market.product.domain.SellingStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSellerDetailDto {
	private Long chatCount;
	private Long likeCount;
	private Long locationId;
	private String locationName;
	private SellingStatus status;
	private String category;
	private LocalDateTime createdAt;
	private String content;
	private Long hits;
	private String title;
	private Long price;
	private Long sellerId;
	private String sellerName;

	public ProductSellerDetailDto(Long chatCount, Long likeCount, Long locationId, String locationName,
		SellingStatus status, String category, LocalDateTime createdAt, String content, Long hits, String title,
		Long price,
		Long sellerId, String sellerName) {
		this.chatCount = chatCount;
		this.likeCount = likeCount;
		this.locationId = locationId;
		this.locationName = locationName;
		this.status = status;
		this.category = category;
		this.createdAt = createdAt;
		this.content = content;
		this.hits = hits;
		this.title = title;
		this.price = price;
		this.sellerId = sellerId;
		this.sellerName = sellerName;
	}
}