package com.carrot.market.product.application;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.global.exception.ApiException;
import com.carrot.market.global.exception.domain.LocationException;
import com.carrot.market.global.exception.domain.ProductException;
import com.carrot.market.image.application.ImageService;
import com.carrot.market.image.domain.Image;
import com.carrot.market.location.domain.Location;
import com.carrot.market.location.infrastructure.LocationRepository;
import com.carrot.market.member.application.MemberService;
import com.carrot.market.member.domain.Member;
import com.carrot.market.member.domain.WishList;
import com.carrot.market.member.infrastructure.MemberRepository;
import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.request.ProductCreateServiceRequest;
import com.carrot.market.product.application.dto.request.ProductUpdateServiceRequest;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductChangeStatusResponse;
import com.carrot.market.product.application.dto.response.ProductCreateServiceResponse;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.ProductSellerDetailDto;
import com.carrot.market.product.application.dto.response.ProductUpdateWishList;
import com.carrot.market.product.application.dto.response.WishListDetailDto;
import com.carrot.market.product.domain.Category;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.domain.ProductDetails;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.CategoryRepository;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.product.infrastructure.QueryProductRepository;
import com.carrot.market.product.infrastructure.dto.request.DetailPageSliceRequestDto;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductCacheService productCacheService;
	private final QueryProductRepository queryProductRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final WishListRepository wishListRepository;
	private final MemberRepository memberRepository;
	private final MemberService memberService;
	private final LocationRepository locationRepository;
	private final ImageService imageService;

	public DetailPageServiceDto getMainPage(Long locationId, Long categoryId, Long next, int size) {

		DetailPageSliceRequestDto deTailPageSliceRequestDto = DetailPageSliceRequestDto.builder()
			.locationId(locationId)
			.categoryId(categoryId)
			.nextProductId(next)
			.pageSize(size)
			.build();

		Slice<DetailPageSliceResponseDto> byDetailPageSliceRequestDto = queryProductRepository.findByDetailPageSliceRequestDto(
			deTailPageSliceRequestDto);

		List<DetailPageSliceResponseDto> products = byDetailPageSliceRequestDto.getContent();
		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeOverPageSize(products, size);

		return new DetailPageServiceDto(products, contentNextId);
	}

	/*
	 * 	페이징 할 때 size + 1만큼 요소를 조회하고 다음 페이지가 있다면 마지막 요소를 제거한다.
	 * */
	private List<DetailPageSliceResponseDto> removeLastIfProductsSizeOverPageSize(
		List<DetailPageSliceResponseDto> content, int size) {
		if (content.size() == size + 1) {
			return popLast(content);
		}
		return content;
	}

	private List<DetailPageSliceResponseDto> popLast(List<DetailPageSliceResponseDto> content) {
		return content.subList(0, content.size() - 1);
	}

	private Long getContentNextId(List<DetailPageSliceResponseDto> content, int pageSize) {
		Long nextContentId = null;
		if (content != null && content.size() == pageSize + 1) {
			nextContentId = content.get(content.size() - 1).getId();
		}
		return nextContentId;
	}

	@Cacheable("categoriesCache")
	public List<CategoryDto> getCategories() {
		return categoryRepository.findAll()
			.stream()
			.map(CategoryDto::from)
			.toList();
	}

	public ProductDetailResponseDto getProduct(Long memberId, Long productId) {
		productCacheService.addViewCntToRedis(productId);

		ProductSellerDetailDto productDetailDto = productRepository.findProductDetailById(productId);
		List<Image> images = productImageRepository.findImagesByProductId(productId);

		if (memberId == null) {
			return ProductDetailResponseDto.from(images, productDetailDto, false);
		}

		Boolean isLiked = wishListRepository.existsWishListByMemberIdAndProductId(memberId, productId);
		return ProductDetailResponseDto.from(images, productDetailDto, isLiked);
	}

	public DetailPageServiceDto getSellingProducts(String status, Long memberId, Long next, int size) {
		DetailPageSliceRequestDto build = DetailPageSliceRequestDto.builder()
			.status(SellingStatus.from(status).name())
			.sellerId(memberId)
			.nextProductId(next)
			.pageSize(size)
			.build();

		Slice<DetailPageSliceResponseDto> byDetailPageSliceRequestDto = queryProductRepository.findByMyDetailPageSliceRequestDto(
			build);

		List<DetailPageSliceResponseDto> products = byDetailPageSliceRequestDto.getContent();
		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeOverPageSize(products, size);

		return new DetailPageServiceDto(products, contentNextId);
	}

	public WishListDetailDto getWishList(Long categoryId, Long memberId, Long next, int size) {
		Member member = memberRepository.findById(memberId).get();
		Set<Category> categories = wishListRepository.findWishListByMember(member)
			.stream()
			.map(WishList::getCategory)
			.collect(Collectors.toSet());

		DetailPageSliceRequestDto build = DetailPageSliceRequestDto.builder()
			.wishMemberId(memberId)
			.categoryId(categoryId)
			.nextProductId(next)
			.pageSize(size)
			.build();

		List<DetailPageSliceResponseDto> products = queryProductRepository.findByMyDetailPageSliceRequestDto(
			build).getContent();

		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeOverPageSize(products, size);

		return WishListDetailDto.from(categories, products, contentNextId);
	}

	@Transactional
	public ProductCreateServiceResponse createProduct(Long memberId, ProductCreateServiceRequest request) {
		Member seller = memberService.findMemberById(memberId);
		Category category = findCategoryById(request.categoryId());
		Location location = findLocationById(request.locationId());
		Product product = request.toEntity(seller, category, location);
		product.addProductImages(imageService.findImagesById(request.imageIds()));
		productRepository.save(product);

		return ProductCreateServiceResponse.from(product);
	}

	private Location findLocationById(Long locationId) {
		return locationRepository.findById(locationId)
			.orElseThrow(() -> new ApiException(LocationException.NOT_FOUND_ID));
	}

	private Category findCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new ApiException(ProductException.NOT_FOUND_CATEGORY));
	}

	@Transactional
	public void updateProduct(Long memberId, Long productId, ProductUpdateServiceRequest request) {
		Member seller = memberService.findMemberById(memberId);
		Product product = findProductById(productId);
		ProductDetails productDetails = request.productDetails();

		if (product.isChangedProductImage(request.imageIds())) {
			var images = imageService.findImagesById(request.imageIds());
			product.update(seller, productDetails, images);
			return;
		}
		product.update(seller, productDetails);
	}

	@Transactional
	public ProductChangeStatusResponse changeProductStatus(Long memberId, Long productId, String status) {
		SellingStatus changeStatus = SellingStatus.from(status);
		Member seller = memberService.findMemberById(memberId);
		Product product = findProductById(productId);
		product.changeStatus(seller, changeStatus);

		return new ProductChangeStatusResponse(productId);
	}

	@Transactional
	public ProductUpdateWishList updateProductWishList(Long memberId, Long productId) {
		Member member = memberService.findMemberById(memberId);
		Product product = findProductById(productId);
		Optional<WishList> wishList = wishListRepository.findByProductAndMember(product, member);
		if (wishList.isEmpty()) {
			wishListRepository.save(new WishList(product, member));
			return new ProductUpdateWishList(true);
		}

		wishListRepository.delete(wishList.get());

		return new ProductUpdateWishList(false);
	}

	@Transactional
	public void removeProduct(Long productId, Long memberId) {
		Member seller = memberService.findMemberById(memberId);
		Product product = findProductById(productId);
		product.validateSeller(seller);
		productRepository.delete(product);
	}

	private Product findProductById(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new ApiException(ProductException.NOT_FOUND_PRODUCT));
	}
}
