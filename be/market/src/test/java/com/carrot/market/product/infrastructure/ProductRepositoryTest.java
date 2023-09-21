package com.carrot.market.product.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.response.ProductSellerDetailDto;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.support.IntegrationTestSupport;

import jakarta.persistence.EntityManager;

class ProductRepositoryTest extends IntegrationTestSupport {
	@Autowired
	ProductRepository productRepository;
	@Autowired
	WishListRepository wishListRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductImageRepository productImageRepository;
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	LocationRepository locationRepository;
	@Autowired
	MemberRepository memberRepository;

	@Autowired
	QueryProductRepository queryProductRepository;
	@Autowired
	ChatroomRepository chatroomRepository;
	@Autowired
	EntityManager entityManager;

	@Test
	void findProductDetailbyId() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean));

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category);
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);

		// when
		ProductSellerDetailDto productDetailById = productRepository.findProductDetailById(product.getId());

		// then
		assertAll(
			() -> assertThat(productDetailById.getCategory()).isEqualTo(product.getCategory().getName()),
			() -> assertThat(productDetailById.getChatCount()).isEqualTo(2L),
			() -> assertThat(productDetailById.getLocationId()).isEqualTo(location.getId()),
			() -> assertThat(productDetailById.getLocationName()).isEqualTo(location.getName()),
			() -> assertThat(productDetailById.getContent()).isEqualTo(product.getProductDetails().getContent()),
			() -> assertThat(productDetailById.getLikeCount()).isEqualTo(1L),
			() -> assertThat(productDetailById.getPrice()).isEqualTo(product.getProductDetails().getPrice()),
			() -> assertThat(productDetailById.getHits()).isEqualTo(product.getProductDetails().getHits()),
			() -> assertThat(productDetailById.getTitle()).isEqualTo(product.getProductDetails().getTitle())
		);
	}

	@Test
	void findCategoryByMemberId() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean));

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category);
		Category category2 = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category2);
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location, image, category2);

		// when
		List<Category> categoryByMemberId = wishListRepository.findWishListByMember(june)
			.stream()
			.map(WishList::getCategory)
			.toList();

		// then
		assertAll(
			() -> assertThat(categoryByMemberId.size()).isEqualTo(2),
			() -> assertThat(categoryByMemberId.get(0).getId()).isEqualTo(category.getId()),
			() -> assertThat(categoryByMemberId.get(0).getName()).isEqualTo(category.getName()),
			() -> assertThat(categoryByMemberId.get(1).getId()).isEqualTo(category2.getId()),
			() -> assertThat(categoryByMemberId.get(1).getName()).isEqualTo(category2.getName())
		);
	}

	private Product makeProductWishListChatRoomProductImage(Member june, Member bean, Location location, Image image,
		Category category) {
		Product product = makeProduct(june, location, category, SellingStatus.SELLING,
			new ProductDetails("title", 3000L, "content", 3000L));
		productRepository.save(product);

		WishList wishList = makeWishList(product, june);
		wishListRepository.save(wishList);

		ProductImage productImage = makeProductImage(product, image, true);
		productImageRepository.save(productImage);

		Chatroom chatroom = makeChatRoom(product, june);
		Chatroom chatroom2 = makeChatRoom(product, bean);
		chatroomRepository.saveAll(List.of(chatroom, chatroom2));
		return product;
	}
}