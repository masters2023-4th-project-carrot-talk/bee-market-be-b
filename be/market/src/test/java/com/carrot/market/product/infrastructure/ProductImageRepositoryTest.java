package com.carrot.market.product.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.support.IntegrationTestSupport;

class ProductImageRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ProductImageRepository productImageRepository;
	@Autowired
	MemberRepository memberRepository;

	@Test
	void findImageUrlsbyPrdcutId() {
		// given
		Member june = makeMember("june", "sadfas");
		memberRepository.save(june);
		Image image = makeImage("www.google.com");
		Image image2 = makeImage("www.naver.com");
		imageRepository.saveAll(List.of(image, image2));
		Product product = Product.builder().seller(june).build();
		productRepository.save(product);

		ProductImage productImage = makeProductImage(product, image, true);
		ProductImage productImage2 = makeProductImage(product, image2, false);
		productImageRepository.saveAll(List.of(productImage, productImage2));

		List<String> imageUrlsbyPrdcutId = productImageRepository.findImageUrlsbyPrdcutId(product.getId());

		assertThat(imageUrlsbyPrdcutId).hasSize(2).containsExactly("www.google.com", "www.naver.com");
	}
}