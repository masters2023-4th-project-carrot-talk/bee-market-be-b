package com.carrot.market.redis;

import java.time.Duration;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisUtil {

	private final StringRedisTemplate stringRedisTemplate;

	public String getData(String key) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		return valueOperations.get(key);
	}

	public void setData(String key, String value, Duration timeout) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.set(key, value, timeout);
	}

	public void setDataExpire(String key, String value) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		Duration expireDuration = Duration.ofDays(3);
		valueOperations.set(key, value, expireDuration);
	}

	public void deleteData(String key) {
		stringRedisTemplate.delete(key);
	}

	public void increment(String key) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.increment(key);
	}

	public Set<String> keys(String pattern) {
		return stringRedisTemplate.keys(pattern);
	}
}