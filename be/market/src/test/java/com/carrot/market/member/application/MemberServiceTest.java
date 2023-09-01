package com.carrot.market.member.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.application.dto.request.SignupServiceRequest;
import com.carrot.market.support.IntegrationTestSupport;

class MemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberService memberService;

	@Autowired
	private LocationRepository locationRepository;

	@DisplayName("회원의 메인 동네를 서브 동네와 바꿀 수 있다.")
	@Test
	void changeMainLocation() {
		//given
		String socialId = "AKSDAKDK";
		Location mainLocation = new Location("locationA");
		Location subLocation = new Location("locationB");
		locationRepository.saveAll(List.of(mainLocation, subLocation));

		var signUpRequest = SignupServiceRequest.builder()
			.socialId(socialId)
			.imageUrl("image")
			.nickname("bean")
			.mainLocationId(mainLocation.getId())
			.subLocationId(subLocation.getId())
			.build();
		memberService.signup(signUpRequest);

		//when
		memberService.updateLocation(socialId, subLocation.getId());

		//then
		var memberLocations = memberService.getRegisteredLocations(socialId);

		assertThat(memberLocations).hasSize(2)
			.extracting("id", "isMainLocation")
			.containsExactlyInAnyOrder(
				tuple(mainLocation.getId(), Boolean.FALSE),
				tuple(subLocation.getId(), Boolean.TRUE));
	}

	@DisplayName("회원 동네 추가")
	@Nested
	class RegisterLocation {
		@DisplayName("회원의 새로운 동네를 추가할 수 있다.")
		@Test
		void registerNewLocation() {
			//given
			String socialId = "AKSDAKDK";
			Location mainLocation = new Location("locationA");
			Location subLocation = new Location("locationB");
			Location newLocation = new Location("LocationC");
			locationRepository.saveAll(List.of(mainLocation, subLocation, newLocation));

			var signUpRequest = SignupServiceRequest.builder()
				.socialId(socialId)
				.imageUrl("image")
				.nickname("bean")
				.mainLocationId(mainLocation.getId())
				.subLocationId(subLocation.getId())
				.build();
			memberService.signup(signUpRequest);
			memberService.removeRegisteredLocation(socialId, subLocation.getId());

			//when
			memberService.updateLocation(socialId, newLocation.getId());

			//then
			var memberLocations = memberService.getRegisteredLocations(socialId);

			assertThat(memberLocations).hasSize(2)
				.extracting("id", "isMainLocation")
				.containsExactlyInAnyOrder(
					tuple(mainLocation.getId(), Boolean.TRUE),
					tuple(newLocation.getId(), Boolean.FALSE)
				);
		}

		@DisplayName("동네가 두개인 경우 새로운 동네를 추가할 수 없다.")
		@Test
		void registerNewLocationFail() {
			//given
			String socialId = "AKSDAKDK";
			Location mainLocation = new Location("locationA");
			Location subLocation = new Location("locationB");
			Location newLocation = new Location("LocationC");
			locationRepository.saveAll(List.of(mainLocation, subLocation, newLocation));

			var signUpRequest = SignupServiceRequest.builder()
				.socialId(socialId)
				.imageUrl("image")
				.nickname("bean")
				.mainLocationId(mainLocation.getId())
				.subLocationId(subLocation.getId())
				.build();
			memberService.signup(signUpRequest);

			//when //then
			assertThatThrownBy(() -> memberService.updateLocation(socialId, newLocation.getId()))
				.isInstanceOf(ApiException.class)
				.hasMessage("새로운 동네를 추가할 수 없습니다.");
		}
	}

	@DisplayName("회원 동네 삭제")
	@Nested
	class RemoveLocation {

		@DisplayName("메인 동네를 삭제하면 서브 동네가 메인이 된다.")
		@Test
		void removeRegisteredLocation() {
			//given
			String socialId = "ASDADSAD";
			Location mainLocation = new Location("locationA");
			Location subLocation = new Location("locationB");
			locationRepository.saveAll(List.of(mainLocation, subLocation));

			var signUpRequest = SignupServiceRequest.builder()
				.socialId(socialId)
				.imageUrl("image")
				.nickname("bean")
				.mainLocationId(mainLocation.getId())
				.subLocationId(subLocation.getId())
				.build();
			memberService.signup(signUpRequest);

			//when
			memberService.removeRegisteredLocation(socialId, mainLocation.getId());

			//then
			var memberLocations = memberService.getRegisteredLocations(socialId);

			assertThat(memberLocations).hasSize(1)
				.extracting("id", "isMainLocation")
				.containsExactly(tuple(subLocation.getId(), Boolean.TRUE));
		}

		@DisplayName("동네가 하나만 있으면 삭제할 수 없다.")
		@Test
		void removeFailIfRegisteredLocationIsOne() {
			//given
			String socialId = "ASDADSAD";
			Location mainLocation = new Location("locationA");
			Location subLocation = new Location("locationB");
			locationRepository.saveAll(List.of(mainLocation, subLocation));

			var signUpRequest = SignupServiceRequest.builder()
				.socialId(socialId)
				.imageUrl("image")
				.nickname("bean")
				.mainLocationId(mainLocation.getId())
				.subLocationId(subLocation.getId())
				.build();
			memberService.signup(signUpRequest);
			memberService.removeRegisteredLocation(socialId, subLocation.getId());

			//when // then
			assertThatThrownBy(() -> memberService.removeRegisteredLocation(socialId, mainLocation.getId()))
				.isInstanceOf(ApiException.class)
				.hasMessage("새로운 동네를 삭제할 수 없습니다.");
		}
	}
}