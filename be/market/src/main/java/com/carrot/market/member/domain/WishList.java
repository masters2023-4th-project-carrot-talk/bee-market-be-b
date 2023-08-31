package com.carrot.market.member.domain;

import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.product.domain.Product;

import jakarta.persistence.Entity;
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
public class WishList extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public WishList(Product product, Member member) {
		setProduct(product);
		setMember(member);
	}

	private void setMember(Member member) {
		this.member = member;
		member.getWishLists().add(this);
	}

	private void setProduct(Product product) {
		this.product = product;
		product.getWishLists().add(this);
	}
}
