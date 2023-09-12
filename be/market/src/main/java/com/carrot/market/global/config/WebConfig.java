package com.carrot.market.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.carrot.market.auth.resolver.OauthArgumentResolver;
import com.carrot.market.member.resolver.MemberArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${http.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(allowedOrigins)
			.allowedMethods(
				HttpMethod.GET.name(),
				HttpMethod.POST.name(),
				HttpMethod.PUT.name(),
				HttpMethod.DELETE.name(),
				HttpMethod.PATCH.name()
			)
			.allowCredentials(true)
			.exposedHeaders("*");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new MemberArgumentResolver());
		resolvers.add(new OauthArgumentResolver());
	}
}
