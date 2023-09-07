package com.carrot.market.product.application;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.ProductSellerDetaillDto;
import com.carrot.market.product.application.dto.response.WishListDetailDto;
import com.carrot.market.product.domain.Category;
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

		ProductSellerDetaillDto productDetailDto = productRepository.findProductDetailbyId(productId);
		List<String> imageUrls = productImageRepository.findImageUrlsbyPrdcutId(productId);

		if (memberId == null) {
			return ProductDetailResponseDto.from(imageUrls, productDetailDto, false);
		}

		Boolean isLiked = wishListRepository.existsWishListByMemberIdAndProductId(memberId, productId);
		return ProductDetailResponseDto.from(imageUrls, productDetailDto, isLiked);
	}

	public DetailPageServiceDto getSellingProducts(String status, Long memberId, Long next, int size) {
		DetailPageSliceRequestDto build = DetailPageSliceRequestDto.builder()
			.status(SellingStatus.fromString(status))
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
		List<Category> categoryByMemberId = productRepository.findCategoryByMemberId(memberId);
		DetailPageSliceRequestDto build = DetailPageSliceRequestDto.builder()
			.wishMemberId(memberId)
			.categoryId(categoryId)
			.nextProductId(next)
			.pageSize(size)
			.build();
		Slice<DetailPageSliceResponseDto> byMyDetailPageSliceRequestDto = queryProductRepository.findByMyDetailPageSliceRequestDto(
			build);
		List<DetailPageSliceResponseDto> products = byMyDetailPageSliceRequestDto.getContent();
		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeOverPageSize(products, size);

		return WishListDetailDto.from(categoryByMemberId, products, contentNextId);
	}

}
