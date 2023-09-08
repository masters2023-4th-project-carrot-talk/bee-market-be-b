package com.carrot.market.member.application;

import static com.carrot.market.product.infrastructure.QueryProductRepository.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.LocationException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.jwt.domain.Jwt;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.application.dto.request.SignupServiceRequest;
import com.carrot.market.member.application.dto.response.LoginMemberResponse;
import com.carrot.market.member.application.dto.response.MainLocationResponse;
import com.carrot.market.member.application.dto.response.MemberLocationResponse;
import com.carrot.market.member.application.dto.response.ReissueAccessTokenResponse;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.MemberLocation;
import com.carrot.market.member.infrastructure.MemberLocationRepository;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.presentation.dto.request.ReissueAccessTokenRequest;
import com.carrot.market.oauth.application.dto.response.LoginResponse;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {
	private static final String MEMBER_ID = "memberId";

	private final MemberRepository memberRepository;
	private final LocationRepository locationRepository;
	private final MemberLocationRepository memberLocationRepository;
	private final JwtProvider jwtProvider;

	@Transactional
	public LoginResponse signup(SignupServiceRequest signupServiceRequest) {
		Member member = signupServiceRequest.toMember();

		Member savedMember = memberRepository.save(member);

		Jwt jwt = jwtProvider.createJwt(Map.of(MEMBER_ID, savedMember.getId()));
		savedMember.setRefreshToken(jwt.refreshToken());
		LoginMemberResponse loginMemberResponse = LoginMemberResponse.from(member);

		saveMainMemberLocation(member, findLocationById(signupServiceRequest.mainLocationId()));

		saveSubMemberLocation(member, findLocationById(signupServiceRequest.subLocationId()));

		return LoginResponse.success(jwt, loginMemberResponse);
	}

	@Transactional
	public MainLocationResponse updateLocation(Long memberId, Long locationId) {
		final Member member = findMemberById(memberId);
		final Location location = findLocationById(locationId);

		if (member.isRegisteredLocation(location)) {
			member.changeMainLocation(location);
			return new MainLocationResponse(member.getMainMemberLocation());
		}
		if (member.isAllRegisteredLocation()) {
			throw new ApiException(MemberException.NOT_REGISTER_LOCATION);
		}

		saveSubMemberLocation(member, location);

		return new MainLocationResponse(member.getMainMemberLocation());
	}

	public List<MemberLocationResponse> getRegisteredLocations(Long memberId) {
		if (memberRepository.findById(memberId).isEmpty()) {
			Location location = locationRepository.findById(BASIC_LOCATION_ID).get();
			return List.of(MemberLocationResponse.fromLocation(location));
		}
		final Member member = findMemberById(memberId);

		return member.getMemberLocations()
			.stream()
			.map(MemberLocationResponse::from)
			.toList();
	}

	@Transactional
	public MainLocationResponse removeRegisteredLocation(Long memberId, Long locationId) {
		final Member member = findMemberById(memberId);
		final Location location = findLocationById(locationId);

		MemberLocation removedLocation = member.removeLocation(location);
		memberLocationRepository.delete(removedLocation);

		return new MainLocationResponse(member.getMainMemberLocation());
	}

	private MemberLocation saveMainMemberLocation(Member member, Location location) {
		return saveMemberLocation(true, member, location);
	}

	private MemberLocation saveSubMemberLocation(Member member, Location location) {
		return saveMemberLocation(false, member, location);
	}

	private MemberLocation saveMemberLocation(boolean isMain, Member member, Location location) {
		return memberLocationRepository.save(
			MemberLocation.builder()
				.isMain(isMain)
				.member(member)
				.location(location)
				.build()
		);
	}

	public boolean checkDuplicateNickname(String nickname) {
		memberRepository.findByNickname(nickname)
			.ifPresent(member -> {
				throw new ApiException(MemberException.EXIST_MEMBER);
			});
		return true;
	}

	private Location findLocationById(Long locationId) {
		return locationRepository.findById(locationId)
			.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID));
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
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

}
