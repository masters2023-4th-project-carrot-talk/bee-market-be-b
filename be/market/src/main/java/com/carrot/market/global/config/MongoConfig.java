package com.carrot.market.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.carrot.market.global.properties.MongoProperties;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "com.carrot.market.chat.infrastructure.mongo")
public class MongoConfig {
	private final MongoProperties mongoProperties;

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create(mongoProperties.getClient());
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), mongoProperties.getName());
	}
}
