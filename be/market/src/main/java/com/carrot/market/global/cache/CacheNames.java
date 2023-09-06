package com.carrot.market.global.cache;

import lombok.Getter;

@Getter
public class CacheNames {
	public static final String PRODUCT_CACHE = "productHits";

	public static String getProductCachePattern() {
		return PRODUCT_CACHE + "*";
	}

	public static String createHitsCacheKey(Long id) {
		return createCacheKey(PRODUCT_CACHE, id);
	}

	public static String createCacheKey(String cacheType, Long id) {
		return cacheType + "::" + id;
	}
}
