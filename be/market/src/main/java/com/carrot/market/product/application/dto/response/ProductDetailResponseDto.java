package com.carrot.market.product.application.dto.response;

import java.util.List;

import com.carrot.market.image.domain.Image;

import lombok.Builder;

@Builder
public record ProductDetailResponseDto(
	List<ImageResponse> images,
	SellerDetailDto seller,
	ProductDetailDto product
) {
	public static ProductDetailResponseDto from(List<Image> images, ProductSellerDetaillDto productDetailDto,
		Boolean isLiked) {
		return ProductDetailResponseDto.builder()
			.images(images.stream()
				.map(image -> new ImageResponse(image.getId(), image.getImageUrl()))
				.toList())
			.seller(SellerDetailDto.from(productDetailDto))
			.product(ProductDetailDto.from(productDetailDto, isLiked))
			.build();
	}
}
