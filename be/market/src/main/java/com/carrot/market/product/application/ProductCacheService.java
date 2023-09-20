package com.carrot.market.product.application;

import static com.carrot.market.global.cache.CacheNames.*;

import java.time.Duration;
import java.util.Objects;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.redis.RedisUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductCacheService {
	public static final int HITS_DURATION = 30;
	private static final int READ_KEY_COUNT = 100;
	public static final int HITS_SCHEDULED_DURATION = 5000;
	private final ProductRepository productRepository;
	private final RedisUtil redisUtil;

	public void addViewCntToRedis(Long productId) {
		String viewCntKey = createHitsCacheKey(productId);
		if (redisUtil.getData(viewCntKey) != null) {
			redisUtil.increment(viewCntKey);
			return;
		}

		redisUtil.setData(
			viewCntKey,
			String.valueOf(1L),
			Duration.ofSeconds(HITS_DURATION)
		);
	}

	/**
	 * 5초 캐시 데이터를 RDB 반영 후 삭제한다
	 */
	@Scheduled(fixedDelay = HITS_SCHEDULED_DURATION)
	@Transactional
	public void applyViewCountToRDB() {
		var hitsKeys = redisUtil.getKeysSortedByExpiration(getProductCachePattern(), READ_KEY_COUNT);

		if (Objects.requireNonNull(hitsKeys).isEmpty()) {
			return;
		}

		for (String hitsKey : hitsKeys) {
			Long boardId = extractBoardIdFromKey(hitsKey);
			Long hits = Long.parseLong(redisUtil.getData(hitsKey));
			productRepository.applyHitsToRDB(boardId, hits);
			redisUtil.deleteData(hitsKey);
		}
	}

	public static Long extractBoardIdFromKey(String key) {
		return Long.parseLong(key.split("::")[1]);
	}
}
