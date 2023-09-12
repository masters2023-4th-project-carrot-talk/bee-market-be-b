package com.carrot.market.product.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.LastModifiedDate;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.global.domain.BaseEntity;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ProductException;
import com.carrot.market.image.domain.Image;
import com.carrot.market.location.domain.Location;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private ProductDetails productDetails;

	@Enumerated(value = EnumType.STRING)
	private SellingStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member seller;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_id")
	private Location location;

	@BatchSize(size = 10)
	@OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<ProductImage> productImages = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE})
	private List<WishList> wishLists = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE})
	private List<Chatroom> chatrooms = new ArrayList<>();

	@LastModifiedDate
	private LocalDateTime modifiedAt;

	@Builder
	public Product(ProductDetails productDetails, SellingStatus status, Category category,
		Member seller, Location location) {
		this.productDetails = productDetails;
		this.status = status;
		this.category = category;
		saveSeller(seller);
		this.location = location;
	}

	private void saveSeller(Member seller) {
		this.seller = seller;
		seller.getProducts().add(this);
	}

	public List<ProductImage> addProductImages(List<Image> images) {
		return images.stream()
			.map(image -> ProductImage.builder()
				.product(this)
				.isMain(isEmptyProductImage())
				.image(image)
				.build()
			).toList();
	}

	public boolean isEmptyProductImage() {
		return productImages.isEmpty();
	}

	public void update(Member seller, ProductDetails productDetails, List<Image> images) {
		update(seller, productDetails);
		productImages = new ArrayList<>(addProductImages(images));
	}

	public void update(Member seller, ProductDetails productDetails) {
		validateSeller(seller);
		this.productDetails = productDetails;
	}

	public void validateSeller(Member seller) {
		if (this.seller != seller) {
			throw new ApiException(ProductException.NOT_AUTHORIZED_UPDATE);
		}
	}

	public boolean isChangedProductImage(List<Long> imageIds) {
		return productImages.stream()
			.noneMatch(productImage -> imageIds.contains(productImage.getImageId()));
	}

	public Long getCategoryId() {
		return category.getId();
	}

	public Long getLocationId() {
		return location.getId();
	}

	public List<Long> getImageIds() {
		return productImages.stream()
			.map(ProductImage::getImageId)
			.toList();
	}

	public void changeStatus(Member seller, SellingStatus changeStatus) {
		validateSeller(seller);
		status = changeStatus;
	}
}
