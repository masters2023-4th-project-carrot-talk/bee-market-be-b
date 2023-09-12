package com.carrot.market.auth.infrastructure.kakao.authcode;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.carrot.market.auth.infrastructure.kakao.config.KakaoOauthConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAuthCodeRequestUrlProvider {

	private final KakaoOauthConfig kakaoOauthConfig;

	public String provide() {
		return UriComponentsBuilder
			.fromUriString("https://kauth.kakao.com/oauth/authorize")
			.queryParam("response_type", "code")
			.queryParam("client_id", kakaoOauthConfig.clientId())
			.queryParam("redirect_uri", kakaoOauthConfig.redirectUri())
			.queryParam("scope", String.join(",", kakaoOauthConfig.scope()))
			.toUriString();
	}
}

