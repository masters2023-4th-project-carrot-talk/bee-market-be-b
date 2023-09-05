package com.carrot.market.product.application;

import static com.carrot.market.global.cache.CacheNames.*;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.redis.RedisUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductCacheService {
	private final ProductRepository productRepository;
	private final RedisUtil redisUtil;

	public void addViewCntToRedis(Long productId) {
		String viewCntKey = createViewCntCacheKey(productId);
		if (redisUtil.getData(viewCntKey) != null) {
			redisUtil.increment(viewCntKey);
			return;
		}

		redisUtil.setData(
			viewCntKey,
			String.valueOf(productRepository.findViewCount(productId) + 1),
			Duration.ofSeconds(30)
		);
	}

	/**
	 * 5초 캐시 데이터를 RDB 반영 후 삭제한다
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	@Transactional
	public void applyViewCountToRDB() {
		Set<String> viewCntKeys = redisUtil.keys("productViewCnt*");
		if (Objects.requireNonNull(viewCntKeys).isEmpty())
			return;
		for (String viewCntKey : viewCntKeys) {
			Long boardId = extractBoardIdFromKey(viewCntKey);
			Long viewCount = Long.parseLong(redisUtil.getData(viewCntKey));
			productRepository.applyViewCntToRDB(boardId, viewCount);
			redisUtil.deleteData(viewCntKey);
		}
	}

	public static Long extractBoardIdFromKey(String key) {
		return Long.parseLong(key.split("::")[1]);
	}
}
