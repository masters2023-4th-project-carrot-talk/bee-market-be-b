package com.carrot.market.member.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import com.carrot.market.global.domain.BaseEntity;
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

	public boolean isRegisteredLocation(Location location) {
		return memberLocations.stream()
			.anyMatch(memberLocation -> memberLocation.isSameLocation(location));
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

	public MemberLocation removeLocation(Location location) {
		MemberLocation removeMemberLocation = memberLocations.stream()
			.filter(memberLocation -> memberLocation.isSameLocation(location))
			.findFirst()
			.orElseThrow();

		memberLocations.remove(removeMemberLocation);
		if (memberLocations.size() == 1) {
			memberLocations.get(0).changeMainStatus(Boolean.TRUE);
		}

		return removeMemberLocation;
	}

	public MemberLocation getMainMemberLocation() {
		return memberLocations.stream()
			.filter(MemberLocation::isMain)
			.findFirst()
			.orElseThrow();
	}
}
