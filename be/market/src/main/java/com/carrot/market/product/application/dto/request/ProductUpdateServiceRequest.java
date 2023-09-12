package com.carrot.market.product.application.dto.request;

import java.util.List;

import com.carrot.market.product.domain.ProductDetails;

import lombok.Builder;

@Builder
public record ProductUpdateServiceRequest(
	List<Long> imageIds,
	ProductDetails productDetails,
	Long categoryId,
	Long locationId
) {
}
