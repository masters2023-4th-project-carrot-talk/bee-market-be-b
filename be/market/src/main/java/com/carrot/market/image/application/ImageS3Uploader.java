package com.carrot.market.image.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;

@RequiredArgsConstructor
@Component
public class ImageS3Uploader {
	private final S3Template s3Template;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	public String upload(BufferedImage image, String key, int resizeHeight, int resizeWidth) throws IOException {
		image = resizeImage(image, resizeHeight, resizeWidth);
		return s3Template.upload(bucketName, key, getImageInputStream(image, StringUtils.getFilenameExtension(key)))
			.getURL()
			.toString();
	}

	BufferedImage resizeImage(BufferedImage originImage, int resizeHeight, int resizeWidth) {
		if (isLessThanResizeValue(originImage, resizeHeight, resizeWidth)) {
			return originImage;
		}
		MarvinImage resizeImage = new MarvinImage(originImage);
		Scale scale = new Scale();
		scale.load();
		scale.setAttribute("newWidth", resizeWidth);
		scale.setAttribute("newHeight", resizeHeight);
		scale.process(resizeImage.clone(), resizeImage);

		return resizeImage.getBufferedImageNoAlpha();
	}

	private static boolean isLessThanResizeValue(BufferedImage originImage, int resizeHeight, int resizeWidth) {
		return originImage.getWidth() < resizeWidth && originImage.getHeight() < resizeHeight;
	}

	private static InputStream getImageInputStream(BufferedImage image, String extension) throws IOException {
		try (ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream()) {
			ImageIO.write(image, extension, imageOutputStream);
			imageOutputStream.flush();
			return new ByteArrayInputStream(imageOutputStream.toByteArray());
		}
	}
}
