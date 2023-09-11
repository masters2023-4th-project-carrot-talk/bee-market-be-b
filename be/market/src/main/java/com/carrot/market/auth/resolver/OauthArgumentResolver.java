package com.carrot.market.auth.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.carrot.market.auth.domain.OauthMember;
import com.carrot.market.auth.presentation.annotation.OauthLogin;

import jakarta.servlet.http.HttpServletRequest;

public class OauthArgumentResolver implements HandlerMethodArgumentResolver {
	private final String SOCIAL_ID = "socialId";
	private final String IMAGE_URL = "imageUrl";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(OauthLogin.class);
		boolean hasUserType = OauthMember.class.isAssignableFrom(parameter.getParameterType());

		return hasLoginAnnotation && hasUserType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		OauthMember oauthUser = new OauthMember((String)request.getAttribute(SOCIAL_ID),
			(String)request.getAttribute(IMAGE_URL));
		return oauthUser;
	}
}
