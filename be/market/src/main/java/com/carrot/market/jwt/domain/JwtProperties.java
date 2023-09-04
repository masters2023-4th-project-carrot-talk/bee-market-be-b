package com.carrot.market.jwt.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	String secretKey,
	String sub,
	String iss
) {
}
