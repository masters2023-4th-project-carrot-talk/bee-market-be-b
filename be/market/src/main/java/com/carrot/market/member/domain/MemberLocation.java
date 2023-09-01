package com.carrot.market.member.domain;

import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.location.domain.Location;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberLocation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private boolean isMain;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_id")
	private Location location;

	@Builder
	public MemberLocation(boolean isMain, Member member, Location location) {
		this.isMain = isMain;
		saveMember(member);
		this.location = location;
	}

	public void saveMember(Member member) {
		this.member = member;
		member.getMemberLocations().add(this);
	}

	public void changeMainStatus(boolean isMain) {
		this.isMain = isMain;
	}

	public boolean isSameLocation(Location location) {
		return this.location == location;
	}
}