package com.carrot.market.product.application.dto.response;

public record SellerDetailDto(
	Long id,
	String nickname
) {
	public static SellerDetailDto from(ProductSellerDetaillDto productDetailDto) {
		return new SellerDetailDto(productDetailDto.sellerId, productDetailDto.sellerName);
	}
}
