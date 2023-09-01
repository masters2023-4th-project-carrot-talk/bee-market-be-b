package com.carrot.market.location.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.support.IntegrationTestSupport;

class LocationServiceTest extends IntegrationTestSupport {

	@Autowired
	private LocationService locationService;

	@Autowired
	private LocationRepository locationRepository;

	@DisplayName("검색 조건으로 동네를 조회할 수 있다.")
	@Test
	void findLocations() {
		//given
		Location location1 = new Location("서울 도봉구");
		Location location2 = new Location("서울 강남구");

		locationRepository.saveAll(List.of(location1, location2));

		//when
		var locations = locationService.findLocations("강남");

		//then
		assertThat(locations)
			.hasSize(1)
			.extracting("name")
			.containsExactlyInAnyOrder("서울 강남구");
	}

	@DisplayName("동네 검색 조회 결과가 없을 경우 빈 리스트가 반환된다.")
	@Test
	void findLocationIsEmpty() {
		//given
		Location location1 = new Location("서울 도봉구");
		Location location2 = new Location("서울 강남구");

		locationRepository.saveAll(List.of(location1, location2));

		//when
		var locations = locationService.findLocations("노원구");

		//then
		assertThat(locations).isEmpty();
	}
}