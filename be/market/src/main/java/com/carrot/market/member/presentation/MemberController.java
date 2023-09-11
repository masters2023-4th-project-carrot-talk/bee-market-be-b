package com.carrot.market.member.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.application.MemberService;
import com.carrot.market.member.application.dto.response.MainLocationResponse;
import com.carrot.market.member.application.dto.response.MemberLocationResponse;
import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.presentation.dto.request.UpdateLocationRequest;
import com.carrot.market.member.resolver.MemberId;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/locations")
	public ApiResponse<List<MemberLocationResponse>> getMemberLocations(
		@Login MemberId memberId
	) {
		return ApiResponse.success(memberService.getRegisteredLocations(memberId.getMemberID()));
	}

	@PatchMapping("/locations")
	public ApiResponse<MainLocationResponse> updateLocation(
		@RequestBody UpdateLocationRequest request,
		@Login MemberId memberId
	) {
		return ApiResponse.success(memberService.updateLocation(memberId.getMemberID(), request.locationId()));
	}

	@DeleteMapping("/locations/{locationId}")
	public ApiResponse<MainLocationResponse> deleteLocation(@PathVariable Long locationId,
		@Login MemberId memberId
	) {
		return ApiResponse.success(memberService.removeRegisteredLocation(
			memberId.getMemberID(), locationId));
	}
}
