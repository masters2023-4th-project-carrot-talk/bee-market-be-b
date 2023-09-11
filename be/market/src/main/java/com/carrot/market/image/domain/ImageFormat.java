package com.carrot.market.image.domain;

import java.util.Arrays;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ImageException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ImageFormat {
	PNG(".png"),
	JPG(".jpg"),
	JPEG(".jpeg"),
	JPE(".jpe");

	private final String extension;

	public static void validate(String key) {
		if (Arrays.stream(values()).anyMatch(format -> key.endsWith(format.extension))) {
			return;
		}

		throw new ApiException(ImageException.IMAGE_UPLOAD_FAILED);
	}
}
