package com.carrot.market.oauth.application;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.OauthException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.jwt.domain.Jwt;
import com.carrot.market.member.application.dto.response.LoginMemberResponse;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.oauth.application.dto.response.LoginResponse;
import com.carrot.market.oauth.client.OauthMemberClientComposite;
import com.carrot.market.oauth.domain.OauthMember;
import com.carrot.market.oauth.infrastructure.kakao.authcode.KakaoAuthCodeRequestUrlProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OauthService {
	private final static String MEMBER_ID = "memberId";
	private final static String SOCIAL_ID = "socialId";
	private final static String IMAGE_URL = "imageUrl";
	private final static String SUPPORTED_TYPE = "kakao";
	private final JwtProvider jwtProvider;
	private final KakaoAuthCodeRequestUrlProvider kakaoAuthCodeRequestUrlProvider;
	private final OauthMemberClientComposite oauthMemberClientComposite;
	private final MemberRepository memberRepository;

	public String getAuthCodeRequestUrl(String oauthServerType) {
		validOauthServerType(oauthServerType);
		return kakaoAuthCodeRequestUrlProvider.provide();
	}

	private void validOauthServerType(String oauthServerType) {
		if (!SUPPORTED_TYPE.equals(oauthServerType)) {
			throw new ApiException(OauthException.UNSUPPORTED_TYPE);
		}
	}

	public LoginResponse login(String authCode) {
		OauthMember oauthMember = oauthMemberClientComposite.fetch(authCode);

		Optional<Member> optionalMember = memberRepository.findBySocialId(oauthMember.getSocialId());

		if (!optionalMember.isPresent()) {
			String accessToken = createAccessToken(oauthMember);
			return LoginResponse.fail(accessToken);
		}

		Member member = optionalMember.get();
		Jwt jwt = createJwt(member);
		LoginMemberResponse loginMemberResponse = new LoginMemberResponse(member.getId(), member.getNickname());

		return LoginResponse.success(jwt, loginMemberResponse);
	}

	private String createAccessToken(OauthMember oauthMember) {
		return jwtProvider.createAccessToken(
			Map.of(
				SOCIAL_ID, oauthMember.getSocialId(),
				IMAGE_URL, oauthMember.getImageUrl()
			)
		);
	}

	private Jwt createJwt(Member member) {
		return jwtProvider.createJwt(Map.of(MEMBER_ID, member.getId()));
	}
}


