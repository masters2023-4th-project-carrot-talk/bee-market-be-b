package com.carrot.market.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	// 커넥션 설정
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info(host);
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		//		redisStandaloneConfiguration.setPassword(password);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {

		// redisTemplate 를 받아와서 set, get, delete 를 사용
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		/*
		 * setKeySerializer, setValueSerializer 설정
		 * redis-cli 을 통해 직접 데이터를 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
		 */
		//        redisTemplate.setKeySerializer(new StringRedisSerializer());
		//        redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());

		return redisTemplate;
	}

}