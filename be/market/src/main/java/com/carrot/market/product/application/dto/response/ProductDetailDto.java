package com.carrot.market.product.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

public record ProductDetailDto(
	String location,
	String status,
	String title,
	String category,
	LocalDateTime createdAt,
	String content,
	Long chatCount,
	Long likeCount,
	Long hits,
	Long price,
	Boolean isLiked
) {

	public static ProductDetailDto from(ProductSellerDetaillDto productDetailDto, Boolean isLiked) {
		return ProductDetailDto.builder().location(productDetailDto.location)
			.status(productDetailDto.status.getText())
			.title(productDetailDto.name)
			.category(productDetailDto.category)
			.createdAt(productDetailDto.createdAt)
			.content(productDetailDto.content)
			.chatCount(productDetailDto.chatCount)
			.likeCount(productDetailDto.likeCount)
			.hits(productDetailDto.hits)
			.price(productDetailDto.price)
			.isLiked(isLiked)
			.build();
	}

	@Builder
	public ProductDetailDto {
	}
}
