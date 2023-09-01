package com.carrot.market.oauth.presentation;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.oauth.application.OauthService;
import com.carrot.market.oauth.application.dto.response.LoginResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class OauthController {

	private final OauthService oauthService;

	@GetMapping("/oauth/{oauthServerType}")
	ResponseEntity<Void> redirectAuthCodeRequestUrl(
		@PathVariable String oauthServerType,
		HttpServletResponse response
	) throws IOException {
		String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthServerType);
		response.sendRedirect(redirectUrl);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/api/users/login")
	ResponseEntity<LoginResponse> login(
		@RequestParam(name = "code") String code
	) {
		LoginResponse login = oauthService.login(code);
		return ResponseEntity.ok(login);
	}

}

