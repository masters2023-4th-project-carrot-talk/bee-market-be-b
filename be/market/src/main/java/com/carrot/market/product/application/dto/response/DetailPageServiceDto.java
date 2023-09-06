package com.carrot.market.product.application.dto.response;

import java.util.List;

import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;

public record DetailPageServiceDto(
	List<DetailPageSliceResponseDto> products,
	Long nextId
) {
}
