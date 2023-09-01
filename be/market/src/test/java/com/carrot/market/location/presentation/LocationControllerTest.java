package com.carrot.market.location.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.carrot.market.location.application.dto.response.LocationResponse;
import com.carrot.market.support.ControllerTestSupport;

class LocationControllerTest extends ControllerTestSupport {

	@DisplayName("동네 정보를 조회할 수 있다.")
	@Test
	void getLocations() throws Exception {
		//given
		var locations = List.of(
			new LocationResponse(1L, "서울 도봉구"),
			new LocationResponse(2L, "서울 강남구"));

		given(locationService.findLocations(anyString())).willReturn(locations);

		//when, then
		mockMvc.perform(get("/api/locations"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].id").isNumber())
			.andExpect(jsonPath("$.data[0].name").isString())
			.andExpect(jsonPath("$.data[1].id").isNumber())
			.andExpect(jsonPath("$.data[1].name").isString());
	}

	@DisplayName("동네 조회 결과가 없으면 빈 리스트가 반환된다.")
	@Test
	void getLocationsEmpty() throws Exception {
		//given
		given(locationService.findLocations(anyString())).willReturn(anyList());

		//when, then
		mockMvc.perform(get("/api/locations"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0]").doesNotExist());
	}
}