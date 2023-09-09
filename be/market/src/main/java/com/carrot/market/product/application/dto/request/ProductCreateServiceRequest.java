package com.carrot.market.product.application.dto.request;

import java.util.List;

import com.carrot.market.location.domain.Location;
import com.carrot.market.member.domain.Member;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.SellingStatus;

import lombok.Builder;

@Builder
public record ProductCreateServiceRequest(
	List<Long> imageIds,
	ProductDetails productDetails,
	Long categoryId,
	Long locationId
) {
	public Product toEntity(Member seller, Category category, Location location) {
		return Product.builder()
			.seller(seller)
			.category(category)
			.location(location)
			.status(SellingStatus.SELLING)
			.productDetails(productDetails)
			.build();
	}
}
