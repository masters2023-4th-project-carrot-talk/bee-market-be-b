package com.carrot.market.product.application.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

public record ProductDetailDto(
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

	public static ProductDetailDto from(ProductSellerDetailDto productDetailDto, Boolean isLiked) {
		return ProductDetailDto.builder()
			.status(productDetailDto.getStatus().getText())
			.title(productDetailDto.getTitle())
			.category(productDetailDto.getCategory())
			.createdAt(productDetailDto.getCreatedAt())
			.content(productDetailDto.getContent())
			.chatCount(productDetailDto.getChatCount())
			.likeCount(productDetailDto.getLikeCount())
			.hits(productDetailDto.getHits())
			.price(productDetailDto.getPrice())
			.isLiked(isLiked)
			.build();
	}

	@Builder
	public ProductDetailDto {
	}
}
