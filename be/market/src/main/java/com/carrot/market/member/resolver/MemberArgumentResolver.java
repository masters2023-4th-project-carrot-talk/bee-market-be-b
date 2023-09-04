package com.carrot.market.member.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.carrot.market.member.presentation.annotation.Login;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class MemberArgumentResolver implements HandlerMethodArgumentResolver {
	private final String MEMBER_ID = "memberId";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasUserType = Long.class.isAssignableFrom(parameter.getParameterType());

		return hasLoginAnnotation && hasUserType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		return request.getAttribute(MEMBER_ID);
	}
}
