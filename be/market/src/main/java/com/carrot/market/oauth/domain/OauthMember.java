package com.carrot.market.oauth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class OauthMember {
	@JsonProperty("socialId")
	private String socialId;
	@JsonProperty("imageUrl")
	private String imageUrl;

	public OauthMember() {
	}

	public OauthMember(String socialId, String imageUrl) {
		this.socialId = socialId;
		this.imageUrl = imageUrl;
	}
}
