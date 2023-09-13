package com.carrot.market.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "mongodb")
public class MongoProperties {
	String client;
	String name;
}
