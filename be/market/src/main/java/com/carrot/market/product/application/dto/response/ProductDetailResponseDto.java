package com.carrot.market.product.application.dto.response;

import java.util.List;

import lombok.Builder;

public record ProductDetailResponseDto(
	List<String> imageUrls,
	SellerDetailDto seller,
	ProductDetailDto product
) {
	public static ProductDetailResponseDto from(List<String> imageUrls, ProductSellerDetaillDto productDetailDto,
		Boolean isLiked) {
		SellerDetailDto seller = SellerDetailDto.from(productDetailDto);
		ProductDetailDto product = ProductDetailDto.from(productDetailDto, isLiked);
		return ProductDetailResponseDto.builder().imageUrls(imageUrls).seller(seller).product(product).build();
	}

	@Builder
	public ProductDetailResponseDto {
	}
}
