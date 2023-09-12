package com.carrot.market.member.application;

import static com.carrot.market.product.infrastructure.QueryProductRepository.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.LocationException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.jwt.application.JwtProvider;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.application.dto.response.MainLocationResponse;
import com.carrot.market.member.application.dto.response.MemberLocationResponse;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.MemberLocation;
import com.carrot.market.member.infrastructure.MemberLocationRepository;
import com.carrot.market.member.infrastructure.MemberRepository;

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

	public MemberLocation saveMainMemberLocation(Member member, Location location) {
		return saveMemberLocation(true, member, location);
	}

	public MemberLocation saveSubMemberLocation(Member member, Location location) {
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

	public Location findLocationById(Long locationId) {
		return locationRepository.findById(locationId)
			.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID));
	}

	public Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new ApiException(MemberException.NOT_FOUND_MEMBER));
	}
}
