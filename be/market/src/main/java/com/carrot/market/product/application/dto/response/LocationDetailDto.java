package com.carrot.market.product.application.dto.response;

public record LocationDetailDto(
	Long id,
	String name
) {
	public static LocationDetailDto from(ProductSellerDetailDto productSellerDetailDto) {
		return new LocationDetailDto(productSellerDetailDto.getLocationId(), productSellerDetailDto.getLocationName());
	}
}
