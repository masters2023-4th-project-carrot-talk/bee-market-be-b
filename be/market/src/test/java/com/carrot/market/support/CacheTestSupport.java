package com.carrot.market.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.scheduling.enable=true")
@ActiveProfiles("test")
@SpringBootTest
public abstract class CacheTestSupport {
}
