package com.carrot.market.chatroom.domain;

import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.member.domain.Member;
import com.carrot.market.product.domain.Product;

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
public class Chatroom extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member purchaser;

	@Builder
	public Chatroom(Product product, Member purchaser) {
		this.product = product;
		this.purchaser = purchaser;
	}

	public Member getReceiver(Member sender) {
		Member seller = product.getSeller();
		if (sender == seller) {
			return purchaser;
		}
		return seller;
	}

	public String getProductTitle() {
		return product.getProductDetails().getTitle();
	}
}
