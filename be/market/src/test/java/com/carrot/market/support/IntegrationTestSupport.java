package com.carrot.market.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;

@TestPropertySource(properties = "app.scheduling.enable=false")
@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {
	@LocalServerPort
	protected int port;

	@BeforeEach
	protected void setUp() {
		RestAssured.port = port;
	}
}
