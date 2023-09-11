package com.carrot.market.auth.client;

import org.springframework.stereotype.Component;

import com.carrot.market.auth.domain.OauthMember;
import com.carrot.market.auth.infrastructure.kakao.KakaoMemberClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OauthMemberClientComposite {

	private final KakaoMemberClient kakaoMemberClient;

	public OauthMember fetch(String authCode) {
		return kakaoMemberClient.fetch(authCode);
	}

}
