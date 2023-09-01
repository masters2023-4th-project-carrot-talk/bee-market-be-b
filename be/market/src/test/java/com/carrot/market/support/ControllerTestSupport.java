package com.carrot.market.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.carrot.market.global.filter.JwtAuthorizationFilter;
import com.carrot.market.location.application.LocationService;
import com.carrot.market.location.presentation.LocationController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
	LocationController.class
}, excludeFilters = {
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthorizationFilter.class)
})
public abstract class ControllerTestSupport {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected LocationService locationService;
}