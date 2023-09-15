package com.carrot.market.chatroom.infrastructure;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.dto.ChatroomResponse;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.infrastructure.WishListRepository;
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

class QueryChatroomRepositoryTest extends IntegrationTestSupport {
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
	void getChattingList() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		Member sully = makeMember("sully", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean, sully));

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		categoryRepository.save(category);
		Product product = makeProduct(june, location, category, SellingStatus.SELLING,
			new ProductDetails("title", 3000L, "content", 3000L));
		Product product2 = makeProduct(june, location, category, SellingStatus.SELLING,
			new ProductDetails("title", 3000L, "content", 3000L));
		productRepository.saveAll(List.of(product, product2));

		ProductImage productImage = makeProductImage(product, image, true);
		ProductImage productImage2 = makeProductImage(product2, image, true);
		productImageRepository.saveAll(List.of(productImage, productImage2));

		Chatroom chatroom = makeChatRoom(product, bean);
		Chatroom chatroom2 = makeChatRoom(product2, sully);
		chatroomRepository.saveAll(List.of(chatroom, chatroom2));
		// when
		List<ChatroomResponse> chattingList = chatroomRepository.getChattingByMemberId(
			june.getId());

		assertThat(
			chattingList
		).hasSize(2)
			.extracting("nickname", "imageUrl", "locationName", "productMainImage", "chatroomId")
			.containsExactly(
				tuple("bean", "www.codesquad.kr", "susongdong", "www.google.com", chatroom.getId()),
				tuple("sully", "www.codesquad.kr", "susongdong", "www.google.com", chatroom2.getId())

			);
	}

}