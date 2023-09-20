package com.carrot.market.product.application.dto.response;

public record LocationDetailDto(
	Long id,
	String name
) {
	public static LocationDetailDto from(ProductSellerDetaillDto productSellerDetaillDto) {
		return new LocationDetailDto(productSellerDetaillDto.locationId, productSellerDetaillDto.getLocationName());
	}
}
