package com.carrot.market.product.presentation.dto.request;

import java.util.List;

import com.carrot.market.product.application.dto.request.ProductCreateServiceRequest;
import com.carrot.market.product.domain.ProductDetails;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProductCreateRequest(
	@Size(min = 1, max = 10, message = "상품 이미지는 최소 1장 ~ 최대 10장까지 가능합니다.")
	List<Long> images,
	@NotBlank(message = "상품 제목은 필수입니다.")
	String title,
	@Positive
	Long categoryId,
	@Positive
	Long locationId,
	String content,
	Long price
) {
	public ProductCreateServiceRequest toProductCreateServiceRequest() {
		return ProductCreateServiceRequest.builder()
			.imageIds(images)
			.productDetails(getProductDetails())
			.categoryId(categoryId)
			.locationId(locationId)
			.build();
	}

	private ProductDetails getProductDetails() {
		return ProductDetails.builder()
			.title(title)
			.content(content)
			.price(price)
			.build();
	}
}
