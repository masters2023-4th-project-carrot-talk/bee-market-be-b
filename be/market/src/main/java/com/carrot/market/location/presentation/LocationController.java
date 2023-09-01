package com.carrot.market.location.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.location.application.LocationService;
import com.carrot.market.location.application.dto.response.LocationResponse;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/locations")
@RequiredArgsConstructor
@RestController
public class LocationController {

	private final LocationService locationService;

	@GetMapping
	public ApiResponse<List<LocationResponse>> getLocations(
		@RequestParam(defaultValue = "서울") String keyword
	) {
		return ApiResponse.success(locationService.findLocations(keyword));
	}
}
