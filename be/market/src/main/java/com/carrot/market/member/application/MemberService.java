package com.carrot.market.member.application;

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
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.MemberLocation;
import com.carrot.market.member.infrastructure.MemberLocationRepository;
import com.carrot.market.member.infrastructure.MemberRepository;
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
		savedMember.setRefreshToken(jwt.getRefreshToken());
		LoginMemberResponse loginMemberResponse = new LoginMemberResponse(savedMember.getId(),
			savedMember.getNickname());

		saveMemberLocation(true, member, signupServiceRequest.mainLocationId());

		saveMemberLocation(false, member, signupServiceRequest.subLocationId());

		return LoginResponse.success(jwt, loginMemberResponse);
	}

	@Transactional
	public MainLocationResponse updateLocation(String socialId, Long locationId) {
		final Member member = memberRepository.findBySocialId(socialId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		final Location location = locationRepository.findById(locationId)
			.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID));

		if (member.isRegisteredLocation(location)) {
			member.changeMainLocation(location);
			return new MainLocationResponse(member.getMainMemberLocation());
		}
		if (member.isAllRegisteredLocation()) {
			throw new ApiException(MemberException.NOT_REGISTER_LOCATION);
		}

		saveMemberLocation(Boolean.FALSE, member, locationId);

		return new MainLocationResponse(member.getMainMemberLocation());
	}

	public List<MemberLocationResponse> getRegisteredLocations(String socialId) {
		final Member member = memberRepository.findBySocialId(socialId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));

		return member.getMemberLocations()
			.stream()
			.map(MemberLocationResponse::from)
			.toList();
	}

	@Transactional
	public MainLocationResponse removeRegisteredLocation(String socialId, Long locationId) {
		final Member member = memberRepository.findBySocialId(socialId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
		final Location location = locationRepository.findById(locationId)
			.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID));

		if (!canRemove(member, location)) {
			throw new ApiException(MemberException.NOT_REMOVE_LOCATION);
		}

		MemberLocation removedLocation = member.removeLocation(location);
		memberLocationRepository.delete(removedLocation);

		return new MainLocationResponse(member.getMainMemberLocation());
	}

	private static boolean canRemove(Member member, Location location) {
		return member.isAllRegisteredLocation() && member.isRegisteredLocation(location);
	}

	private MemberLocation saveMemberLocation(boolean isMain, Member member, Long locationId) {
		return memberLocationRepository.save(
			MemberLocation.builder()
				.isMain(isMain)
				.member(member)
				.location(locationRepository.findById(locationId)
					.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID)))
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
}
