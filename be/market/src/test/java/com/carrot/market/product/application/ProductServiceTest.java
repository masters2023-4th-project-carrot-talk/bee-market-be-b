package com.carrot.market.product.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.config.QuerydslConfig;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.response.MainPageServiceDto;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.CategoryRepository;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.product.infrastructure.QueryProductRepository;
import com.carrot.market.support.IntegrationTestSupport;

import jakarta.persistence.EntityManager;

@Import(QuerydslConfig.class)
class ProductServiceTest extends IntegrationTestSupport {
	@Autowired
	ProductService productService;
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
	void getMainPageWithNextIdNull() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		memberRepository.save(june);
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.save(bean);

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category);
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		productRepository.save(product);
		// when
		MainPageServiceDto mainPage = productService.getMainPage(location.getId(), category.getId(), null, 1);

		// then
		assertAll(
			() -> assertThat(mainPage.products().size()).isEqualTo(1),
			() -> assertThat(mainPage.nextId()).isEqualTo(null)
		);

	}

	@Test
	void getMainPageWithNextIdNotNull() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		memberRepository.save(june);
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.save(bean);

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category);
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		productRepository.save(product);
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		productRepository.save(product2);
		// when
		MainPageServiceDto mainPage = productService.getMainPage(location.getId(), category.getId(), null, 1);

		// then
		assertAll(
			() -> assertThat(mainPage.products().size()).isEqualTo(1),
			() -> assertThat(mainPage.nextId()).isEqualTo(product.getId())
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
		chatroomRepository.save(chatroom);
		Chatroom chatroom2 = makeChatRoom(product, bean);
		chatroomRepository.save(chatroom2);
		return product;

	}
}