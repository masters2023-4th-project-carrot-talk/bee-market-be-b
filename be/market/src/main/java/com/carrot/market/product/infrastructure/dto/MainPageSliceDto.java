package com.carrot.market.product.infrastructure.dto;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class MainPageSliceDto {
	Long id;
	Long sellerId;
	String name;
	String location;
	String imageUrl;
	LocalDateTime createdAt;
	Long price;
	String status;
	Long likeCount;
	Long chatCount;

	@QueryProjection
	public MainPageSliceDto(Long id, Long sellerId, String name, String location, String imageUrl,
		LocalDateTime createdAt, Long price, String status, Long likeCount, Long chatCount) {
		this.id = id;
		this.sellerId = sellerId;
		this.name = name;
		this.location = location;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.price = price;
		this.status = status;
		this.likeCount = likeCount;
		this.chatCount = chatCount;
	}

}
