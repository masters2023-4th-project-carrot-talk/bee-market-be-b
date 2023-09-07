package com.carrot.market.oauth.presentation;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.oauth.application.OauthService;
import com.carrot.market.oauth.application.dto.response.LoginResponse;
import com.carrot.market.oauth.presentation.dto.request.LoginRequest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OauthController {

	private final OauthService oauthService;

	@GetMapping("/oauth/{oauthServerType}")
	ApiResponse<Void> redirectAuthCodeRequestUrl(
		@PathVariable String oauthServerType,
		HttpServletResponse response
	) throws IOException {
		String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);
		response.sendRedirect(redirectUrl);
		return ApiResponse.successNoBody();
	}

	@PostMapping("/api/users/login")
	ApiResponse<LoginResponse> login(
		@RequestBody LoginRequest loginRequest
	) {
		LoginResponse login = oauthService.login(loginRequest.code());
		return ApiResponse.success(login);
	}

}

