package com.carrot.market.image.application.dto.response;

import com.carrot.market.image.domain.Image;

public record ImageResponse(
	Long imageId,
	String imageUrl
) {
	public static ImageResponse from(Image image) {
		return new ImageResponse(
			image.getId(),
			image.getImageUrl()
		);
	}
}
