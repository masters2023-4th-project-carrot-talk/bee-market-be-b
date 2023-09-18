package com.carrot.market.member.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.LastModifiedDate;

import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.MemberException;
import com.carrot.market.location.domain.Location;
import com.carrot.market.product.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String nickname;

	private String imageUrl;

	@OneToMany(mappedBy = "member")
	private List<MemberLocation> memberLocations = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<WishList> wishLists = new ArrayList<>();

	@OneToMany(mappedBy = "seller")
	private List<Product> products = new ArrayList<>();

	private String refreshToken;

	private String socialId;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	@Builder
	public Member(String nickname, String imageUrl, String refreshToken, String socialId, LocalDateTime modifiedAt) {
		this.nickname = nickname;
		this.imageUrl = imageUrl;
		this.refreshToken = refreshToken;
		this.socialId = socialId;
		this.modifiedAt = modifiedAt;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void changeMainLocation(Location location) {
		for (MemberLocation memberLocation : memberLocations) {
			if (memberLocation.isSameLocation(location)) {
				memberLocation.changeMainStatus(Boolean.TRUE);
				continue;
			}

			memberLocation.changeMainStatus(Boolean.FALSE);
		}
	}

	public boolean isAllRegisteredLocation() {
		return memberLocations.size() == 2;
	}

	public boolean isRegisteredLocation(Location location) {
		return memberLocations.stream()
			.anyMatch(memberLocation -> memberLocation.isSameLocation(location));
	}

	public MemberLocation removeLocation(Location location) {
		if (!canRemove(location)) {
			throw new ApiException(MemberException.NOT_REMOVE_LOCATION);
		}

		MemberLocation removeMemberLocation = findMemberLocationBy(location);
		memberLocations.remove(removeMemberLocation);
		getSubMemberLocation().ifPresent(memberLocation -> memberLocation.changeMainStatus(Boolean.TRUE));

		return removeMemberLocation;
	}

	private MemberLocation findMemberLocationBy(Location location) {
		return memberLocations.stream()
			.filter(memberLocation -> memberLocation.isSameLocation(location))
			.findFirst()
			.orElseThrow(() -> new ApiException(MemberException.NOT_REGISTER_LOCATION));
	}

	private boolean canRemove(Location location) {
		return isAllRegisteredLocation() && isRegisteredLocation(location);
	}

	public MemberLocation getMainMemberLocation() {
		return getMemberLocation(Boolean.TRUE)
			.orElseThrow(() -> new ApiException(MemberException.NOT_EXISTS_MAIN_LOCATION));
	}

	public Optional<MemberLocation> getSubMemberLocation() {
		return getMemberLocation(Boolean.FALSE);
	}

	public Optional<MemberLocation> getMemberLocation(boolean isMain) {
		return memberLocations.stream()
			.filter(memberLocation -> memberLocation.isMain() == isMain)
			.findFirst();
	}

	public MemberLocation addMainLocation(Location location) {
		MemberLocation mainLocation = MemberLocation.builder().isMain(true).location(location).member(this).build();
		return mainLocation;
	}

	public MemberLocation addSubLocation(Location location) {
		MemberLocation subLocation = MemberLocation.builder().isMain(false).location(location).member(this).build();
		return subLocation;
	}
}
