package com.carrot.market.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {
	private StringRedisTemplate stringRedisTemplate;
	private ValueOperations<String, String> valueOperations;

	public RedisUtil(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.valueOperations = stringRedisTemplate.opsForValue();
	}

	public String getData(String key) {
		return valueOperations.get(key);
	}

	public void setData(String key, String value, Duration timeout) {
		valueOperations.set(key, value, timeout);
	}

	public void setDataExpire(String key, String value) {
		Duration expireDuration = Duration.ofDays(3);
		valueOperations.set(key, value, expireDuration);
	}

	public void deleteData(String key) {
		stringRedisTemplate.delete(key);
	}

	public void increment(String key) {
		valueOperations.increment(key);
	}

	public Set<String> keys(String pattern) {
		return stringRedisTemplate.keys(pattern);
	}

	public List<String> getKeysSortedByExpiration(String pattern, int count) {
		ScanOptions options = ScanOptions.scanOptions()
			.match(pattern)
			.count(count)
			.build();
		Map<String, Long> keyExpireTimeMap = new HashMap<>();

		try (Cursor<String> cursor = stringRedisTemplate.scan(options)) {
			while (cursor.hasNext()) {
				String key = cursor.next();
				Long expiration = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
				if (expiration != null && expiration > 0) {
					keyExpireTimeMap.put(key, expiration);
				}
			}
		}
		List<String> sortedKeys = new ArrayList<>(keyExpireTimeMap.keySet());
		sortedKeys.sort(Comparator.comparingLong(keyExpireTimeMap::get));

		return sortedKeys;
	}
}