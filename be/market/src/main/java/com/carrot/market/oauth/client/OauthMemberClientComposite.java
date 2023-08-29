package com.carrot.market.oauth.client;

import org.springframework.stereotype.Component;

import com.carrot.market.oauth.domain.OauthMember;
import com.carrot.market.oauth.infrastructure.kakao.KakaoMemberClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OauthMemberClientComposite {

	private final KakaoMemberClient kakaoMemberClient;

	public OauthMember fetch(String authCode) {
		return kakaoMemberClient.fetch(authCode);
	}

}
