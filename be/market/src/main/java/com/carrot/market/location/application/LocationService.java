package com.carrot.market.location.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.location.application.dto.response.LocationResponse;
import com.carrot.market.location.infrastructure.LocationRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LocationService {

	private final LocationRepository locationRepository;

	public List<LocationResponse> findLocations(String keyword) {
		return locationRepository.findByNameContaining(keyword)
			.stream()
			.map(location -> new LocationResponse(
				location.getId(),
				location.getName())
			).toList();
	}
}
