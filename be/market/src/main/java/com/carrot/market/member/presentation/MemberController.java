package com.carrot.market.member.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.application.MemberService;
import com.carrot.market.member.presentation.dto.request.SignupRequest;
import com.carrot.market.oauth.application.dto.response.LoginResponse;
import com.carrot.market.oauth.domain.OauthMember;
import com.carrot.market.oauth.presentation.annotation.OauthLogin;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	@GetMapping()
	ApiResponse<LoginResponse> checkDuplicateNickname(
		@RequestParam String nickname
	) {
		memberService.checkDuplicateNickname(nickname);
		return ApiResponse.successNoBody();
	}

	@PostMapping("/signup")
	ApiResponse<LoginResponse> signup(
		@RequestBody SignupRequest signupRequest,
		@OauthLogin OauthMember oauthUser
	) {
		LoginResponse response = memberService.signup(
			signupRequest.toSignupServiceRequest(oauthUser.getImageUrl(), oauthUser.getSocialId()));

		return ApiResponse.success(response);
	}
}
