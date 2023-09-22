package com.carrot.market.chatroom.application.dto.response;

import com.carrot.market.image.domain.Image;
import com.carrot.market.product.domain.Product;

public record ChattingProductResponse(
	Long id,
	String title,
	Long price,
	String thumbnail
) {
	public static ChattingProductResponse from(Product product, Image thumbnail) {
		return new ChattingProductResponse(product.getId(), product.getProductDetails().getTitle(),
			product.getProductDetails().getPrice(), thumbnail.getImageUrl());
	}
}
