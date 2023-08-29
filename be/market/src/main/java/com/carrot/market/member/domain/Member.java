package com.carrot.market.member.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import com.carrot.market.global.domain.BaseEntity;

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

	private String nickname;

	private String imageUrl;

	@OneToMany(mappedBy = "member")
	private List<MemberLocation> memberLocations = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<WishList> wishLists = new ArrayList<>();

	@OneToMany(mappedBy = "member")
	private List<Sales> sales = new ArrayList<>();

	private String refreshToken;

	private String socialId;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Builder
	public Member(String nickname, String imageUrl,
		List<MemberLocation> memberLocations, List<WishList> wishLists,
		List<Sales> sales, String refreshToken, String socialId, LocalDateTime modifiedAt) {
		this.nickname = nickname;
		this.imageUrl = imageUrl;
		this.memberLocations = memberLocations;
		this.wishLists = wishLists;
		this.sales = sales;
		this.refreshToken = refreshToken;
		this.socialId = socialId;
		this.modifiedAt = modifiedAt;
	}
}
