package com.carrot.market.product.application.dto.response;

import java.util.List;

import com.carrot.market.product.domain.Category;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;

public record WishListDetailDto(
	List<CategoryDto> categories,
	List<DetailPageSliceResponseDto> products,
	Long nextId
) {

	public static WishListDetailDto from(List<Category> categories, List<DetailPageSliceResponseDto> products,
		Long nextId) {
		return new WishListDetailDto(categories.stream().map(CategoryDto::from).toList(), products, nextId);
	}
}
