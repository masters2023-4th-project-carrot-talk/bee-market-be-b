package com.carrot.market.product.application.dto.response;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.carrot.market.product.domain.Category;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;

public record WishListDetailDto(
	List<CategoryDto> categories,
	List<DetailPageSliceResponseDto> products,
	Long nextId
) {

	public static WishListDetailDto from(Set<Category> categories, List<DetailPageSliceResponseDto> products,
		Long nextId) {
		List<CategoryDto> categoryDtos = categories.stream()
			.map(CategoryDto::from)
			.sorted(Comparator.comparing(CategoryDto::id))
			.toList();

		return new WishListDetailDto(categoryDtos, products, nextId);
	}
}
