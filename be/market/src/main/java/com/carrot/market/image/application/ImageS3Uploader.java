package com.carrot.market.image.application;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ImageException;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ImageS3Uploader {
	private static final String EXTENSION_SEPARATOR = ".";
	private static final String DIRECTORY_SEPARATOR = "/";

	private final S3Template s3Template;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	public String upload(MultipartFile file, String fileDir) {
		try (var inputStream = file.getInputStream()) {
			String key = generateKey(file, fileDir);
			ObjectMetadata metadata = createMetadata(file.getContentType());

			return s3Template.upload(bucketName, key, inputStream, metadata)
				.getURL()
				.toString();
		} catch (IOException ex) {
			throw new ApiException(ImageException.IMAGE_UPLOAD_FAILED);
		}
	}

	private String generateKey(MultipartFile file, String fileDir) {
		String fileName = UUID.randomUUID().toString();
		String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		return fileDir + DIRECTORY_SEPARATOR + fileName + EXTENSION_SEPARATOR + extension;
	}

	private ObjectMetadata createMetadata(String contentType) {
		return ObjectMetadata.builder()
			.contentType(contentType)
			.build();
	}
}
