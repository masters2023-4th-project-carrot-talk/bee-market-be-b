package com.carrot.market.product.domain;

import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.image.domain.Image;

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
public class ProductImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "image_id")
	private Image image;

	private boolean isMain;

	@Builder
	public ProductImage(Product product, Image image, boolean isMain) {
		setProduct(product);
		this.image = image;
		this.isMain = isMain;
	}

	private void setProduct(Product product) {
		this.product = product;
		product.getProductImages().add(this);
	}

}
