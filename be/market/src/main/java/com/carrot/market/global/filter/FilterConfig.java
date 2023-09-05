package com.carrot.market.global.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.carrot.market.jwt.application.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class FilterConfig {
	private final JwtProvider jwtProvider;

	@Bean
	JwtAuthorizationFilter filter(ObjectMapper objectMapper) {
		return new JwtAuthorizationFilter(objectMapper, jwtProvider);
	}

	@Bean
	public FilterRegistrationBean<Filter> jwtAuthorizationFilter(JwtAuthorizationFilter jwtAuthorizationFilter) {
		FilterRegistrationBean<Filter> filterRegistrationBean = new
			FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(jwtAuthorizationFilter);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
}


