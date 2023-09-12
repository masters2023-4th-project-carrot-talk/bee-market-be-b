package com.carrot.market.auth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OauthMember {
	@JsonProperty("socialId")
	private String socialId;
	@JsonProperty("imageUrl")
	private String imageUrl;

}
