package com.carrot.market.auth.application;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.auth.application.dto.response.LoginResponse;
import com.carrot.market.auth.client.OauthMemberClientComposite;
import com.carrot.market.auth.domain.OauthMember;
import com.carrot.market.auth.infrastructure.kakao.authcode.KakaoAuthCodeRequestUrlProvider;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.global.exception.domain.OauthException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.jwt.domain.Jwt;
import com.carrot.market.member.application.MemberService;
import com.carrot.market.member.application.dto.request.SignupServiceRequest;
import com.carrot.market.member.application.dto.response.LoginMemberResponse;
import com.carrot.market.member.application.dto.response.ReissueAccessTokenResponse;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.presentation.dto.request.ReissueAccessTokenRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AuthService {
	private final static String MEMBER_ID = "memberId";
	private final static String SOCIAL_ID = "socialId";
	private final static String IMAGE_URL = "imageUrl";
	private final static String SUPPORTED_TYPE = "kakao";

	private final JwtProvider jwtProvider;
	private final KakaoAuthCodeRequestUrlProvider kakaoAuthCodeRequestUrlProvider;
	private final OauthMemberClientComposite oauthMemberClientComposite;
	private final MemberRepository memberRepository;
	private final MemberService memberService;

	@Transactional
	public LoginResponse signup(SignupServiceRequest request) {
		Member member = request.toMember();
		Long mainLocationId = request.mainLocationId();
		Long subLocationId = request.subLocationId();
		Member savedMember = memberService.registerMemberWithLocation(member, mainLocationId, subLocationId);

		Jwt jwt = jwtProvider.createJwt(Map.of(MEMBER_ID, savedMember.getId()));
		savedMember.setRefreshToken(jwt.refreshToken());
		LoginMemberResponse loginMemberResponse = LoginMemberResponse.from(member);

		return LoginResponse.success(jwt, loginMemberResponse);
	}

	@Transactional
	public LoginResponse login(String authCode) {
		OauthMember oauthMember = oauthMemberClientComposite.fetch(authCode);

		Optional<Member> optionalMember = memberRepository.findBySocialId(oauthMember.getSocialId());

		if (!optionalMember.isPresent()) {
			String accessToken = createAccessToken(oauthMember);
			return LoginResponse.fail(accessToken);
		}

		Member member = optionalMember.get();
		Jwt jwt = createJwt(member);
		memberRepository.updateRefreshTokenByUserIdAndRefreshToken(member.getId(), jwt.refreshToken());
		LoginMemberResponse loginMemberResponse = LoginMemberResponse.from(member);

		return LoginResponse.success(jwt, loginMemberResponse);
	}

	public ReissueAccessTokenResponse reissueAccessToken(ReissueAccessTokenRequest reissueAccessTokenRequest) {
		String refreshToken = reissueAccessTokenRequest.refreshToken();
		Member member = memberRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		Jwt jwt = jwtProvider.reissueAccessToken(Map.of(MEMBER_ID, member.getId()), refreshToken);
		return new ReissueAccessTokenResponse(jwt.accessToken());
	}

	@Transactional
	public void logout(Long memberId, String refreshToken) {
		memberRepository.updateRefreshTokenNullByUserIdAndRefreshToken(memberId, refreshToken);
	}

	public String getAuthCodeRequestUrl(String oauthServerType) {
		validOauthServerType(oauthServerType);
		return kakaoAuthCodeRequestUrlProvider.provide();
	}

	private void validOauthServerType(String oauthServerType) {
		if (!SUPPORTED_TYPE.equals(oauthServerType)) {
			throw new ApiException(OauthException.UNSUPPORTED_TYPE);
		}
	}

	public boolean checkDuplicateNickname(String nickname) {
		memberRepository.findByNickname(nickname)
			.ifPresent(member -> {
				throw new ApiException(MemberException.EXIST_MEMBER);
			});
		return true;
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


