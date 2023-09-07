package com.carrot.market.image.application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ImageException;
import com.carrot.market.image.application.dto.response.ImageResponse;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ImageService {
	private static final String EXTENSION_SEPARATOR = ".";

	@Value("${upload.image.directory}")
	private String uploadDirectory;

	@Value("${upload.image.width}")
	private int imageWidth;

	@Value("${upload.image.height}")
	private int imageHeight;

	private final ImageRepository imageRepository;

	private final ImageS3Uploader imageS3Uploader;

	@Transactional
	public List<ImageResponse> uploadImages(List<MultipartFile> multipartFiles) {
		final List<String> imageUrls = new ArrayList<>();

		for (MultipartFile file : multipartFiles) {
			imageUrls.add(uploadImage(file));
		}

		final List<Image> images = Image.createImages(imageUrls);
		imageRepository.saveAll(images);

		return images.stream()
			.map(ImageResponse::from)
			.toList();
	}

	private String uploadImage(MultipartFile file) {
		try (var imageInputStream = file.getInputStream()) {
			BufferedImage image = ImageIO.read(imageInputStream);
			return imageS3Uploader.upload(image, generateKey(file.getOriginalFilename()), imageHeight, imageWidth);
		} catch (IOException e) {
			throw new ApiException(ImageException.IMAGE_UPLOAD_FAILED);
		}
	}

	private String generateKey(String originFilename) {
		String fileName = UUID.randomUUID().toString();
		String extension = StringUtils.getFilenameExtension(originFilename);
		return uploadDirectory + fileName + EXTENSION_SEPARATOR + extension;
	}
}
