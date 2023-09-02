package com.carrot.market.product.application.dto.response;

import com.carrot.market.product.domain.Category;

public record CategoryDto(
	Long id,
	String name,
	String imageUrl
) {
	public static CategoryDto from(Category category) {
		return new CategoryDto(category.getId(), category.getName(), category.getImageUrl());
	}
}
