package com.carrot.market.product.application.dto.response;

import java.util.List;

import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;

public record MainPageServiceDto(
	List<MainPageSliceDto> products,
	Long nextId
) {
}
