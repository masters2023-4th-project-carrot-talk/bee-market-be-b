package com.carrot.market.image.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.market.image.application.dto.response.ImageResponse;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ImageService {
	private static final String UPLOAD_DIRECTORY = "products";

	private final ImageRepository imageRepository;
	private final ImageS3Uploader imageS3Uploader;

	@Transactional
	public List<ImageResponse> uploadImages(List<MultipartFile> multipartFiles) {
		final List<String> imageUrls = new ArrayList<>();

		for (MultipartFile file : multipartFiles) {
			String uploadedImageUrl = imageS3Uploader.upload(file, UPLOAD_DIRECTORY);
			imageUrls.add(uploadedImageUrl);
		}

		final List<Image> images = Image.createImages(imageUrls);
		imageRepository.saveAll(images);

		return images.stream()
			.map(ImageResponse::from)
			.toList();
	}
}
