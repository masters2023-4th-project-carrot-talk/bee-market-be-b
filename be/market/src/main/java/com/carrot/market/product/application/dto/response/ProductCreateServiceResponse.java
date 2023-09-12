package com.carrot.market.product.application.dto.response;

import java.util.List;

import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;

import lombok.Builder;

@Builder
public record ProductCreateServiceResponse(
	Long id,
	List<Long> imageIds,
	ProductDetails productDetails,
	Long categoryId,
	Long locationId
) {
	public static ProductCreateServiceResponse from(Product product) {
		return ProductCreateServiceResponse.builder()
			.id(product.getId())
			.productDetails(product.getProductDetails())
			.imageIds(product.getImageIds())
			.categoryId(product.getCategoryId())
			.locationId(product.getLocationId())
			.build();
	}
}
