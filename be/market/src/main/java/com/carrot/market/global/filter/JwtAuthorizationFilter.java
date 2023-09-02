package com.carrot.market.global.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsUtils;

import com.carrot.market.global.exception.domain.JwtException;
import com.carrot.market.global.exception.response.ErrorResponse;
import com.carrot.market.jwt.application.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter implements Filter {
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String MEMBER_ID = "memberId";
	private static final String SOCIAL_ID = "socialId";
	private static final String PROFILE_IMAGE_URL = "imageUrl";
	private static final String[] whiteListUris = {"/oauth/**", "/api/users/**"};
	private static final String SIGNUP_URI = "/api/users/signup";

	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper;

	public JwtAuthorizationFilter(ObjectMapper objectMapper, JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
		this.objectMapper = objectMapper;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		if (CorsUtils.isPreFlightRequest(httpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		if (whiteListCheck(httpServletRequest.getRequestURI())) {
			chain.doFilter(request, response);
			return;
		}

		if (!isContainToken(httpServletRequest)) {
			sendErrorApiResponse(response, new MalformedJwtException(""));
			return;
		}

		try {
			String token = getToken(httpServletRequest);
			Claims claims = jwtProvider.getClaims(token);
			request.setAttribute(MEMBER_ID, claims.get(MEMBER_ID));
			if (SIGNUP_URI.equals(httpServletRequest.getRequestURI())) {
				setOauthLoginClaim(request, claims);
			}
			chain.doFilter(request, response);
		} catch (RuntimeException e) {
			sendErrorApiResponse(response, e);
		}
	}

	private void setOauthLoginClaim(ServletRequest request, Claims claims) {
		request.setAttribute(SOCIAL_ID, claims.get(SOCIAL_ID));
		request.setAttribute(PROFILE_IMAGE_URL, claims.get(PROFILE_IMAGE_URL));
	}

	private boolean whiteListCheck(String uri) {
		return PatternMatchUtils.simpleMatch(whiteListUris, uri);
	}

	private boolean isContainToken(HttpServletRequest request) {
		String authorization = request.getHeader(HEADER_AUTHORIZATION);
		return authorization != null && authorization.startsWith(TOKEN_PREFIX);
	}

	private String getToken(HttpServletRequest request) {
		String authorization = request.getHeader(HEADER_AUTHORIZATION);
		return authorization.substring(7).replace("\"", "");
	}

	private void sendErrorApiResponse(ServletResponse response, RuntimeException ex) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		((HttpServletResponse)response).setStatus(HttpStatus.UNAUTHORIZED.value());

		response.getWriter().write(
			objectMapper.writeValueAsString(
				generateErrorApiResponse(ex))
		);
	}

	private ErrorResponse generateErrorApiResponse(RuntimeException ex) {
		JwtException jwtException = JwtException.from(ex);
		return ErrorResponse.fail(jwtException.getHttpStatus().value(), jwtException.getMessage());
	}
}
