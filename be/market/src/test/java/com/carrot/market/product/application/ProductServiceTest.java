package com.carrot.market.product.application;

import static com.carrot.market.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.chatroom.domain.Chatroom;
import com.carrot.market.chatroom.infrastructure.ChatroomRepository;
import com.carrot.market.config.QuerydslConfig;
import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.ProductException;
import com.carrot.market.image.domain.Image;
import com.carrot.market.image.infrastructure.ImageRepository;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.request.ProductCreateServiceRequest;
import com.carrot.market.product.application.dto.request.ProductUpdateServiceRequest;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductDetailDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.SellerDetailDto;
import com.carrot.market.product.application.dto.response.WishListDetailDto;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.ProductImage;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.CategoryRepository;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.product.infrastructure.QueryProductRepository;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;
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

	@DisplayName("관심 상품을 취소 할 수 있다.")
	@Test
	public void removeWishList() throws Exception {
		//given
		var seller = makeMember("bean", "image-url");
		var wisher = makeMember("june", "iamge-url");
		var location = makeLocation("도봉구");
		var category = makeCategory("dress", "www.naver.com");
		var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
		var productDetails = makeProductDetails("상품 판매", "본문");

		memberRepository.saveAll(List.of(seller, wisher));
		locationRepository.save(location);
		categoryRepository.save(category);
		imageRepository.saveAllAndFlush(images);

		var imageIds = images.stream().map(Image::getId).toList();
		var request = makeProductCreateRequest(location, category, productDetails, imageIds);
		Long productId = productService.createProduct(seller.getId(), request).id();

		//when
		productService.updateProductWishList(wisher.getId(), productId);
		productService.updateProductWishList(wisher.getId(), productId);
		entityManager.flush();
		entityManager.clear();
		Product product = productRepository.findById(productId).get();
		wisher = memberRepository.findById(wisher.getId()).get();

		//then
		assertThat(wishListRepository.findByProductAndMember(product, wisher)).isEmpty();
	}

	@DisplayName("관심 상품을 추가 할 수 있다.")
	@Test
	public void addWishList() {
		//given
		var seller = makeMember("bean", "image-url");
		var wisher = makeMember("june", "iamge-url");
		var location = makeLocation("도봉구");
		var category = makeCategory("dress", "www.naver.com");
		var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
		var productDetails = makeProductDetails("상품 판매", "본문");

		memberRepository.saveAll(List.of(seller, wisher));
		locationRepository.save(location);
		categoryRepository.save(category);
		imageRepository.saveAllAndFlush(images);

		var imageIds = images.stream().map(Image::getId).toList();
		var request = makeProductCreateRequest(location, category, productDetails, imageIds);
		Long productId = productService.createProduct(seller.getId(), request).id();

		//when
		productService.updateProductWishList(wisher.getId(), productId);
		entityManager.flush();
		entityManager.clear();
		Product product = productRepository.findById(productId).get();
		wisher = memberRepository.findById(wisher.getId()).get();

		//then
		var findWishList = wishListRepository.findByProductAndMember(product, wisher);
		assertThat(findWishList).isPresent();
		assertThat(wisher.getWishLists()).contains(findWishList.get());
	}

	@DisplayName("상품의 상태를 변경할 수 있다.")
	@Test
	public void changeProductStatus() {
		//given
		var seller = makeMember("bean", "image-url");
		var location = makeLocation("도봉구");
		var category = makeCategory("dress", "www.naver.com");
		var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
		var productDetails = makeProductDetails("상품 판매", "본문");

		memberRepository.save(seller);
		locationRepository.save(location);
		categoryRepository.save(category);
		imageRepository.saveAllAndFlush(images);

		var imageIds = images.stream().map(Image::getId).toList();
		var request = makeProductCreateRequest(location, category, productDetails, imageIds);
		Long productId = productService.createProduct(seller.getId(), request).id();
		Product product = productRepository.findById(productId).get();

		SellingStatus beforeStatus = product.getStatus();
		//when
		productService.changeProductStatus(seller.getId(), product.getId(), SellingStatus.SOLD_OUT.getText());
		entityManager.flush();
		entityManager.clear();

		//then
		assertThat(beforeStatus).isNotEqualByComparingTo(product.getStatus());
	}

	@DisplayName("판매자가 아닌 사람이 상품의 상태를 변경하면 NOT_AUTHORIZED_UPDATE 예외가 발생한다.")
	@Test
	public void changeProductStatusOnlySeller() {
		//given
		var seller = makeMember("bean", "image-url");
		var nonSeller = makeMember("june", "image-url");
		var location = makeLocation("도봉구");
		var category = makeCategory("dress", "www.naver.com");
		var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
		var productDetails = makeProductDetails("상품 판매", "본문");

		memberRepository.saveAll(List.of(seller, nonSeller));
		locationRepository.save(location);
		categoryRepository.save(category);
		imageRepository.saveAllAndFlush(images);

		var imageIds = images.stream().map(Image::getId).toList();
		var request = makeProductCreateRequest(location, category, productDetails, imageIds);
		Long productId = productService.createProduct(seller.getId(), request).id();

		//when //then
		assertThatThrownBy(
			() -> productService.changeProductStatus(nonSeller.getId(), productId, SellingStatus.SOLD_OUT.getText()))
			.isInstanceOf(ApiException.class)
			.extracting("message", "status")
			.containsExactly(
				ProductException.NOT_AUTHORIZED_UPDATE.getMessage(),
				ProductException.NOT_AUTHORIZED_UPDATE.getHttpStatus().value());
	}

	@Test
	void getMainPageWithNextIdNull() {
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
		productRepository.save(product);
		// when
		DetailPageServiceDto mainPage = productService.getMainPage(location.getId(), category.getId(), null, 1);

		// then
		assertAll(
			() -> assertThat(mainPage.products().size()).isEqualTo(1),
			() -> assertThat(mainPage.nextId()).isEqualTo(null)
		);

	}

	@Test
	void getMainPageNull() {
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
		// when
		DetailPageServiceDto mainPage = productService.getMainPage(location.getId(), category.getId(), null, 1);

		// then
		assertAll(
			() -> assertThat(mainPage.products().size()).isEqualTo(0),
			() -> assertThat(mainPage.nextId()).isEqualTo(null)
		);

	}

	@Test
	void getMainPageWithNextIdNotNull() {
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
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		productRepository.saveAll(List.of(product, product2));
		// when
		DetailPageServiceDto mainPage = productService.getMainPage(location.getId(), category.getId(), null, 1);

		// then
		assertAll(
			() -> assertThat(mainPage.products().size()).isEqualTo(1),
			() -> assertThat(mainPage.nextId()).isEqualTo(product.getId())
		);

	}

	@Test
	void getCategories() {
		// given
		Category category = makeCategory("category", "www.naver.com");
		Category category2 = makeCategory("category123", "www.naver.com");
		Category category3 = makeCategory("category1233", "www.naver.com");
		categoryRepository.saveAll(List.of(category, category2, category3));

		// when
		List<CategoryDto> categories = productService.getCategories();
		// then
		assertThat(categories).hasSize(3)
			.extracting("name", "imageUrl")
			.contains(
				tuple(category.getName(), category.getImageUrl()),
				tuple(category2.getName(), category2.getImageUrl()),
				tuple(category3.getName(), category3.getImageUrl())
			);
	}

	@Test
	void getPrductDetail() {
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
		productRepository.save(product);
		// when
		ProductDetailResponseDto productDetailResponseDto = productService.getProduct(june.getId(), product.getId());
		// then
		SellerDetailDto seller = productDetailResponseDto.seller();
		ProductDetailDto productDetailDto = productDetailResponseDto.product();
		assertAll(
			() -> assertThat(productDetailResponseDto.imageUrls().get(0)).isEqualTo(image.getImageUrl()),
			() -> assertThat(seller.id()).isEqualTo(june.getId()),
			() -> assertThat(seller.nickname()).isEqualTo(june.getNickname()),
			() -> assertThat(productDetailDto.location()).isEqualTo(location.getName()),
			() -> assertThat(productDetailDto.status()).isEqualTo(SellingStatus.SELLING.getText()),
			() -> assertThat(productDetailDto.title()).isEqualTo(product.getProductDetails().getName()),
			() -> assertThat(productDetailDto.category()).isEqualTo(category.getName()),
			() -> assertThat(productDetailDto.createdAt()).isEqualTo(product.getCreatedAt()),
			() -> assertThat(productDetailDto.content()).isEqualTo(product.getProductDetails().getContent()),
			() -> assertThat(productDetailDto.chatCount()).isEqualTo(2),
			() -> assertThat(productDetailDto.likeCount()).isEqualTo(1),
			() -> assertThat(productDetailDto.price()).isEqualTo(product.getProductDetails().getPrice()),
			() -> assertThat(productDetailDto.isLiked()).isTrue());

	}

	@Test
	void getSellingProductsNull() {
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
		productRepository.save(product);
		// when
		DetailPageServiceDto sellingProducts = productService.getSellingProducts(SellingStatus.SELLING.getText(),
			june.getId(), null, 1);
		// then
		List<DetailPageSliceResponseDto> products = sellingProducts.products();
		Long nextId = sellingProducts.nextId();
		assertAll(
			() -> assertThat(products).hasSize(1),
			() -> assertThat(nextId).isEqualTo(null)
		);
	}

	@Test
	void getSellingProductsWithSelling() {
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
		Product product2 = Product.builder().seller(june).status(SellingStatus.RESERVED).build();
		productRepository.saveAll(List.of(product, product2));
		// when
		DetailPageServiceDto sellingProducts = productService.getSellingProducts(SellingStatus.SELLING.getText(),
			june.getId(), null, 1);
		// then
		List<DetailPageSliceResponseDto> products = sellingProducts.products();
		Long nextId = sellingProducts.nextId();
		assertAll(
			() -> assertThat(products).hasSize(1),
			() -> assertThat(nextId).isEqualTo(null)
		);
	}

	@Test
	void getWishList() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean));

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		Category category2 = makeCategory("dress", "www.naver.com");
		categoryRepository.saveAll(List.of(category, category2));
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location, image, category2);
		productRepository.saveAll(List.of(product, product2));
		// when
		WishListDetailDto wishList = productService.getWishList(null, june.getId(), null, 10);
		// then
		List<DetailPageSliceResponseDto> products = wishList.products();
		Long nextId = wishList.nextId();
		List<CategoryDto> categories = wishList.categories();
		assertAll(
			() -> assertThat(categories).hasSize(2).extracting("id", "name")
				.contains(
					tuple(category.getId(), category.getName()),
					tuple(category2.getId(), category2.getName())
				),
			() -> assertThat(products).hasSize(2),
			() -> assertThat(nextId).isEqualTo(null)
		);
	}

	@Test
	void getWishListWithCategoryId() {
		// given
		Member june = makeMember("june", "www.codesquad.kr");
		Member bean = makeMember("bean", "www.codesquad.kr");
		memberRepository.saveAll(List.of(june, bean));

		Location location = makeLocation("susongdong");
		locationRepository.save(location);

		Image image = makeImage("www.google.com");
		imageRepository.save(image);

		Category category = makeCategory("dress", "www.naver.com");
		Category category2 = makeCategory("dress", "www.naver.com");
		categoryRepository.saveAll(List.of(category, category2));
		Product product = makeProductWishListChatRoomProductImage(june, bean, location, image, category);
		Product product2 = makeProductWishListChatRoomProductImage(june, bean, location, image, category2);
		productRepository.saveAll(List.of(product, product2));
		// when
		WishListDetailDto wishList = productService.getWishList(category.getId(), june.getId(), null, 10);
		// then
		List<DetailPageSliceResponseDto> products = wishList.products();
		Long nextId = wishList.nextId();
		List<CategoryDto> categories = wishList.categories();
		assertAll(
			() -> assertThat(categories).hasSize(2).extracting("id", "name")
				.contains(
					tuple(category.getId(), category.getName()),
					tuple(category2.getId(), category2.getName())
				),
			() -> assertThat(products).hasSize(1),
			() -> assertThat(nextId).isEqualTo(null)
		);
	}

	@Transactional
	@DisplayName("새로운 상품을 등록한다.")
	@Test
	void createProduct() {
		//given
		var seller = makeMember("bean", "image-url");
		var location = makeLocation("도봉구");
		var category = makeCategory("dress", "www.naver.com");
		var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
		var productDetails = makeProductDetails("상품 판매", "본문");

		memberRepository.save(seller);
		locationRepository.save(location);
		categoryRepository.save(category);
		imageRepository.saveAllAndFlush(images);

		var imageIds = images.stream().map(Image::getId).toList();
		var request = makeProductCreateRequest(location, category, productDetails, imageIds);

		//when
		var productResponse = assertDoesNotThrow(() -> productService.createProduct(seller.getId(), request));

		//then
		assertThat(productResponse.id()).isPositive();
		assertThat(productResponse.productDetails()).isEqualTo(productDetails);
		assertThat(productResponse.imageIds()).isEqualTo(imageIds);
		assertThat(productResponse.locationId()).isEqualTo(location.getId());
		assertThat(productResponse.categoryId()).isEqualTo(category.getId());
	}

	@Transactional
	@DisplayName("상품 수정")
	@Nested
	class updateProduct {

		@DisplayName("상품을 수정할 수 있다.")
		@Test
		void updateProduct() {
			//given
			var seller = makeMember("bean", "image-url");
			var location = makeLocation("도봉구");
			var category = makeCategory("dress", "www.naver.com");
			var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
			var productDetails = makeProductDetails("상품 판매", "본문");

			memberRepository.save(seller);
			locationRepository.save(location);
			categoryRepository.save(category);
			imageRepository.saveAllAndFlush(images);

			var imageIds = images.stream().map(Image::getId).toList();
			var createResponse = productService.createProduct(seller.getId(),
				makeProductCreateRequest(location, category, productDetails, imageIds));

			var updateRequest = makeProductUpdateRequest(location, category, imageIds,
				makeProductDetails("제목변경", "내용변경"));

			//when //then
			assertDoesNotThrow(() -> productService.updateProduct(seller.getId(), createResponse.id(), updateRequest));
		}

		@DisplayName("판매자가 아닌 사람이 상품 수정을 하면 NOT_AUTHORIZAED_UPDATE 예외가 발생한다.")
		@Test
		void updateProductOnlySeller() {
			//given
			var seller = makeMember("bean", "image-url");
			var nonSeller = makeMember("june", "image-url");
			var location = makeLocation("도봉구");
			var category = makeCategory("dress", "www.naver.com");
			var images = List.of(makeImage("image1"), makeImage("image2"), makeImage("image3"));
			var productDetails = makeProductDetails("상품 판매", "본문");

			memberRepository.saveAll(List.of(seller, nonSeller));
			locationRepository.save(location);
			categoryRepository.save(category);
			imageRepository.saveAllAndFlush(images);

			var imageIds = images.stream().map(Image::getId).toList();
			var createResponse = productService.createProduct(seller.getId(),
				makeProductCreateRequest(location, category, productDetails, imageIds));
			var updateRequest = makeProductUpdateRequest(location, category, imageIds,
				makeProductDetails("제목변경", "내용변경"));

			//when //then
			assertThatThrownBy(
				() -> productService.updateProduct(nonSeller.getId(), createResponse.id(), updateRequest))
				.isInstanceOf(ApiException.class)
				.extracting("message", "status")
				.containsExactly(
					ProductException.NOT_AUTHORIZED_UPDATE.getMessage(),
					ProductException.NOT_AUTHORIZED_UPDATE.getHttpStatus().value()
				);
		}
	}

	private static ProductUpdateServiceRequest makeProductUpdateRequest(Location location, Category category,
		List<Long> imageIds, ProductDetails productDetails) {

		return ProductUpdateServiceRequest.builder()
			.imageIds(imageIds)
			.productDetails(makeProductDetails(productDetails.getName(), productDetails.getContent()))
			.locationId(location.getId())
			.categoryId(category.getId())
			.build();
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

	private static ProductCreateServiceRequest makeProductCreateRequest(Location location, Category category,
		ProductDetails productDetails, List<Long> imageIds) {
		return ProductCreateServiceRequest.builder()
			.imageIds(imageIds)
			.productDetails(productDetails)
			.categoryId(category.getId())
			.locationId(location.getId())
			.build();
	}

	private static ProductDetails makeProductDetails(String name, String content) {
		return ProductDetails.builder()
			.name(name)
			.content(content)
			.build();
	}
}