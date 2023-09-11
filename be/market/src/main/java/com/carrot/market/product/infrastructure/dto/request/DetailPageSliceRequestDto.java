package com.carrot.market.product.infrastructure.dto.request;

import lombok.Builder;

public record DetailPageSliceRequestDto(
	Long categoryId,
	Long nextProductId,
	String status,
	int pageSize,
	Long locationId,
	Long sellerId,
	Long wishMemberId

) {
	@Builder
	public DetailPageSliceRequestDto {
	}
}
