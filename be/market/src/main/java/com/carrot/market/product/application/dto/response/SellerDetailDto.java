package com.carrot.market.product.application.dto.response;

public record SellerDetailDto(
	Long id,
	String nickname
) {
	public static SellerDetailDto from(ProductSellerDetailDto productDetailDto) {
		return new SellerDetailDto(productDetailDto.getSellerId(), productDetailDto.getSellerName());
	}
}
