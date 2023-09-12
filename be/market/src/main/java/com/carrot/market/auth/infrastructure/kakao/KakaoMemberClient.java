package com.carrot.market.auth.infrastructure.kakao;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.carrot.market.auth.domain.OauthMember;
import com.carrot.market.auth.infrastructure.kakao.client.KakaoApiClient;
import com.carrot.market.auth.infrastructure.kakao.config.KakaoOauthConfig;
import com.carrot.market.auth.infrastructure.kakao.dto.KakaoMemberResponse;
import com.carrot.market.auth.infrastructure.kakao.dto.KakaoToken;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoMemberClient {

	private final KakaoApiClient kakaoApiClient;
	private final KakaoOauthConfig kakaoOauthConfig;

	public OauthMember fetch(String authCode) {
		KakaoToken tokenInfo = kakaoApiClient.fetchToken(tokenRequestParams(authCode));
		KakaoMemberResponse kakaoMemberResponse =
			kakaoApiClient.fetchMember("Bearer " + tokenInfo.accessToken());
		return kakaoMemberResponse.toDomain();
	}

	private MultiValueMap<String, String> tokenRequestParams(String authCode) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoOauthConfig.clientId());
		params.add("redirect_uri", kakaoOauthConfig.redirectUri());
		params.add("code", authCode);
		params.add("client_secret", kakaoOauthConfig.clientSecret());
		return params;
	}
}

