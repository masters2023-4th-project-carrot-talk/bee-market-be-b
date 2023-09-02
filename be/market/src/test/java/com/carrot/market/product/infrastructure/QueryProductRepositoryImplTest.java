package com.carrot.market.product.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

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
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;
import com.carrot.market.support.IntegrationTestSupport;

import jakarta.persistence.EntityManager;

@Import(QuerydslConfig.class)
class QueryProductRepositoryImplTest extends IntegrationTestSupport {

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
	void findByLocationIdAndCategoryIdSliceWithOneProduct() {

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
		Slice<MainPageSliceDto> byLocationIdAndCategoryIdSlice = queryProductRepository.findByLocationIdAndCategoryId(
			location.getId(), category.getId(), null, 10);

		MainPageSliceDto product1 = byLocationIdAndCategoryIdSlice.getContent().get(0);

		// then
		assertAll(
			() -> assertThat(byLocationIdAndCategoryIdSlice.getContent().size()).isEqualTo(1),
			() -> assertThat(product1.getId()).isEqualTo(product.getId()),
			() -> assertThat(product1.getSellerId()).isEqualTo(june.getId()),
			() -> assertThat(product1.getName()).isEqualTo(product.getProductDetails().getName()),
			() -> assertThat(product1.getLocation()).isEqualTo(product.getLocation().getName()),
			() -> assertThat(product1.getCreatedAt()).isEqualTo(product.getCreatedAt()),
			() -> assertThat(product1.getPrice()).isEqualTo(product.getProductDetails().getPrice()),
			() -> assertThat(product1.getStatus()).isEqualTo(product.getStatus().name()),
			() -> assertThat(product1.getImageUrl()).isEqualTo(image.getImageUrl()),
			() -> assertThat(product1.getLikeCount()).isEqualTo(product.getWishLists().size()),
			() -> assertThat(product1.getChatCount()).isEqualTo(2)

		);

	}

	@Test
	void findByLocationIdAndCategoryIdSliceWithTwoProduct() {

		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean));

		Location location = makeLocation("susongdong");
		Location location2 = makeLocation("gangnam");
		locationRepository.saveAll(List.of(location, location2));

		Image image = makeImage("www.google.com");
		imageRepository.save(image);
		Category category = makeCategory("dress", "www.naver.com");
		Category category2 = makeCategory("soccer", "www.naver.com");
		categoryRepository.saveAll(List.of(category, category2));
		Product product1 = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location2, image, category2);

		// when
		Slice<MainPageSliceDto> byLocationIdAndCategoryIdSlice = queryProductRepository.findByLocationIdAndCategoryId(
			location.getId(), category.getId(), null, 10);

		MainPageSliceDto mainPageSliceDto1 = byLocationIdAndCategoryIdSlice.getContent().get(0);
		Slice<MainPageSliceDto> byLocationIdAndCategoryIdSlice2 = queryProductRepository.findByLocationIdAndCategoryId(
			location2.getId(), category2.getId(), null, 10);
		MainPageSliceDto mainPageSliceDto2 = byLocationIdAndCategoryIdSlice2.getContent().get(0);

		// then
		assertAll(
			() -> assertThat(mainPageSliceDto1.getId()).isEqualTo(product1.getId()),
			() -> assertThat(mainPageSliceDto1.getSellerId()).isEqualTo(june.getId()),
			() -> assertThat(mainPageSliceDto1.getName()).isEqualTo(product1.getProductDetails().getName()),
			() -> assertThat(mainPageSliceDto1.getLocation()).isEqualTo(product1.getLocation().getName()),
			() -> assertThat(mainPageSliceDto1.getCreatedAt()).isEqualTo(product1.getCreatedAt()),
			() -> assertThat(mainPageSliceDto1.getPrice()).isEqualTo(product1.getProductDetails().getPrice()),
			() -> assertThat(mainPageSliceDto1.getStatus()).isEqualTo(product1.getStatus().name()),
			() -> assertThat(mainPageSliceDto1.getImageUrl()).isEqualTo(image.getImageUrl()),
			() -> assertThat(mainPageSliceDto1.getLikeCount()).isEqualTo(product1.getWishLists().size()),
			() -> assertThat(mainPageSliceDto1.getChatCount()).isEqualTo(2),
			() -> assertThat(mainPageSliceDto2.getId()).isEqualTo(product2.getId()),
			() -> assertThat(mainPageSliceDto2.getSellerId()).isEqualTo(june.getId()),
			() -> assertThat(mainPageSliceDto2.getName()).isEqualTo(product2.getProductDetails().getName()),
			() -> assertThat(mainPageSliceDto2.getLocation()).isEqualTo(product2.getLocation().getName()),
			() -> assertThat(mainPageSliceDto2.getCreatedAt()).isEqualTo(product2.getCreatedAt()),
			() -> assertThat(mainPageSliceDto2.getPrice()).isEqualTo(product2.getProductDetails().getPrice()),
			() -> assertThat(mainPageSliceDto2.getStatus()).isEqualTo(product2.getStatus().name()),
			() -> assertThat(mainPageSliceDto2.getImageUrl()).isEqualTo(image.getImageUrl()),
			() -> assertThat(mainPageSliceDto2.getLikeCount()).isEqualTo(product2.getWishLists().size()),
			() -> assertThat(mainPageSliceDto2.getChatCount()).isEqualTo(2)

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