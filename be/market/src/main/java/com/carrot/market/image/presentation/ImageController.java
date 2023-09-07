package com.carrot.market.image.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.image.application.ImageService;
import com.carrot.market.image.application.dto.response.ImageResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/images")
@RequiredArgsConstructor
@RestController
public class ImageController {
	private final ImageService imageService;

	@PostMapping
	public ApiResponse<List<ImageResponse>> uploadImages(
		@RequestPart List<MultipartFile> images
	) {
		return ApiResponse.success(imageService.uploadImages(images));
	}
}
