package com.carrot.market.auth.presentation;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.auth.application.AuthService;
import com.carrot.market.auth.application.dto.response.LoginResponse;
import com.carrot.market.auth.domain.OauthMember;
import com.carrot.market.auth.presentation.annotation.OauthLogin;
import com.carrot.market.auth.presentation.dto.request.LoginRequest;
import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.application.dto.response.ReissueAccessTokenResponse;
import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.presentation.dto.request.LogoutRequest;
import com.carrot.market.member.presentation.dto.request.ReissueAccessTokenRequest;
import com.carrot.market.member.presentation.dto.request.SignupRequest;
import com.carrot.market.member.resolver.MemberId;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AuthController {

	private final AuthService authService;

	@GetMapping("/api/users/nickname")
	ApiResponse<LoginResponse> checkDuplicateNickname(
		@RequestParam String nickname
	) {
		authService.checkDuplicateNickname(nickname);
		return ApiResponse.successNoBody();
	}

	@PostMapping("/api/users/login")
	ApiResponse<LoginResponse> login(
		@RequestBody LoginRequest loginRequest
	) {
		LoginResponse login = authService.login(loginRequest.code());
		return ApiResponse.success(login);
	}

	@PostMapping("/api/users/signup")
	ApiResponse<LoginResponse> signup(
		@RequestBody SignupRequest signupRequest,
		@OauthLogin OauthMember oauthUser
	) {
		LoginResponse response = authService.signup(
			signupRequest.toSignupServiceRequest(oauthUser.getImageUrl(), oauthUser.getSocialId()));

		return ApiResponse.success(response);
	}

	@PostMapping("/api/users/reissue-access-token")
	ApiResponse<ReissueAccessTokenResponse> reissueAccessToken(
		@RequestBody ReissueAccessTokenRequest reissueAccessTokenRequest
	) {
		ReissueAccessTokenResponse reissueAccessTokenResponse = authService.reissueAccessToken(
			reissueAccessTokenRequest);
		return ApiResponse.success(reissueAccessTokenResponse);
	}

	@PostMapping("/api/users/logout")
	public ApiResponse<Void> logout(
		@RequestBody LogoutRequest logoutRequest,
		@Login MemberId memberId
	) {
		authService.logout(memberId.getMemberID(), logoutRequest.refreshToken());
		return ApiResponse.successNoBody();
	}

	@GetMapping("/oauth/{oauthServerType}")
	ResponseEntity<?> redirectAuthCodeRequestUrl(
		@PathVariable String oauthServerType
	) {
		String redirectUrl = authService.getAuthCodeRequestUrl(oauthServerType);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(redirectUrl));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
}

