package com.carrot.market.product.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static com.carrot.market.global.cache.CacheNames.*;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.redis.RedisUtil;
import com.carrot.market.support.CacheTestSupport;

import jakarta.persistence.EntityManager;

class ProductCacheServiceTest extends CacheTestSupport {
	@SpyBean
	ProductCacheService productCacheService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	RedisUtil redisUtil;
	@Autowired
	EntityManager entityManager;

	@AfterEach
	void tearDown() {
		productRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		deleteAllInRedis();
	}

	@Test
	void addViewCntToRedis() {
		// given
		Member june = makeMember("june", "www.naver.com");
		memberRepository.save(june);
		Product product = Product.builder()
			.seller(june)
			.productDetails(ProductDetails.builder().hits(0L).build())
			.build();
		productRepository.save(product);
		Long productId = product.getId();

		// when
		productCacheService.addViewCntToRedis(productId);
		productCacheService.addViewCntToRedis(productId);

		// then
		String viewCntCacheKey = createHitsCacheKey(productId);
		Long viewCount = Long.parseLong(redisUtil.getData(viewCntCacheKey));
		assertThat(viewCount).isEqualTo(2L);
	}

	@Test
	void checkSizeViewCntInRedis() {
		// given
		Member june = makeMember("june", "www.naver.com");
		memberRepository.save(june);
		Product product = Product.builder()
			.seller(june)
			.productDetails(ProductDetails.builder().hits(0L).build())
			.build();
		productRepository.save(product);
		Product product2 = Product.builder()
			.seller(june)
			.productDetails(ProductDetails.builder().hits(0L).build())
			.build();
		productRepository.save(product2);
		Long productId = product.getId();
		Long productId2 = product2.getId();

		// when
		productCacheService.addViewCntToRedis(productId);
		productCacheService.addViewCntToRedis(productId2);

		// then
		assertThat(redisUtil.keys(getProductCachePattern())).hasSize(2);
	}

	@Test
	void validateApplyViewCountToRDB() {
		// given
		Member june = makeMember("june", "www.naver.com");
		memberRepository.save(june);
		Product product = Product.builder()
			.seller(june)
			.productDetails(ProductDetails.builder().hits(0L).build())
			.build();
		productRepository.save(product);
		productCacheService.addViewCntToRedis(product.getId());
		productCacheService.addViewCntToRedis(product.getId());

		// when
		await().atMost(10, TimeUnit.SECONDS)
			.untilAsserted(
				() -> verify(productCacheService, atLeast(1)).applyViewCountToRDB());

		// then
		Product byId = productRepository.findById(product.getId()).get();
		assertThat(byId.getProductDetails().getHits()).isEqualTo(2L);
	}

	private void deleteAllInRedis() {
		Set<String> keys = redisUtil.keys(getProductCachePattern());
		for (var key : keys) {
			redisUtil.deleteData(key);
		}
	}
}