package com.carrot.market.auth.infrastructure.kakao.client;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.carrot.market.auth.infrastructure.kakao.dto.KakaoMemberResponse;
import com.carrot.market.auth.infrastructure.kakao.dto.KakaoToken;

public interface KakaoApiClient {

	@PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
	KakaoToken fetchToken(@RequestParam(name = "params") MultiValueMap<String, String> params);

	@GetExchange("https://kapi.kakao.com/v2/user/me")
	KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken);
}

