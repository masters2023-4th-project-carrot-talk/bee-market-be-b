package com.carrot.market.product.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static com.carrot.market.global.cache.CacheNames.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.redis.RedisUtil;
import com.carrot.market.support.CacheTestSupport;

import jakarta.persistence.EntityManager;

class ProductCacheServiceTest extends CacheTestSupport {
	@Autowired
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

	}

	@Transactional
	@Test
	void addViewCntToRedis() {
		// given
		Member june = makeMember("june", "www.naver.com");
		memberRepository.save(june);
		Product product = Product.builder().seller(june).viewCount(0L).build();
		productRepository.save(product);
		Long productId = 1L;

		// when
		productCacheService.addViewCntToRedis(productId);
		productCacheService.addViewCntToRedis(productId);

		// then
		String viewCntCacheKey = createViewCntCacheKey(productId);
		Long viewCount = Long.parseLong(redisUtil.getData(viewCntCacheKey));
		assertThat(viewCount).isEqualTo(2L);
	}

	@Test
	void applyViewCountToRDB() throws InterruptedException {
		// given
		Member june = makeMember("june", "www.naver.com");
		memberRepository.save(june);
		Product product = Product.builder().seller(june).viewCount(0L).build();
		productRepository.save(product);
		productCacheService.addViewCntToRedis(product.getId());
		productCacheService.addViewCntToRedis(product.getId());

		// when
		Thread.sleep(10000L);

		// then
		Product byId = productRepository.findById(product.getId()).get();
		assertThat(byId.getViewCount()).isEqualTo(2L);
	}

}