package com.carrot.market.product.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.member.infrastructure.WishListRepository;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.MainPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.ProductSellerDetaillDto;
import com.carrot.market.product.infrastructure.CategoryRepository;
import com.carrot.market.product.infrastructure.ProductImageRepository;
import com.carrot.market.product.infrastructure.ProductRepository;
import com.carrot.market.product.infrastructure.QueryProductRepository;
import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
	private final QueryProductRepository queryProductRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final WishListRepository wishListRepository;

	public MainPageServiceDto getMainPage(Long locationId, Long categoryId, Long next, int size) {
		Slice<MainPageSliceDto> byLocationIdAndCategoryId = queryProductRepository.findByLocationIdAndCategoryId(
			locationId, categoryId, next, size);
		List<MainPageSliceDto> products = byLocationIdAndCategoryId.getContent();
		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeOverPageSize(products, size);
		return new MainPageServiceDto(products, contentNextId);
	}

	/*
	 * 	페이징 할 때 size + 1만큼 요소를 조회하고 다음 페이지가 있다면 마지막 요소를 제거한다.
	 * */
	private List<MainPageSliceDto> removeLastIfProductsSizeOverPageSize(List<MainPageSliceDto> content, int size) {
		if (content.size() == size + 1) {
			return popLast(content);
		}
		return content;
	}

	private List<MainPageSliceDto> popLast(List<MainPageSliceDto> content) {
		return content.subList(0, content.size() - 1);
	}

	private Long getContentNextId(List<MainPageSliceDto> content, int pageSize) {
		Long nextContentId = null;
		if (content != null && content.size() == pageSize + 1) {
			nextContentId = content.get(content.size() - 1).getId();
		}
		return nextContentId;
	}

	public List<CategoryDto> getCategories() {
		return categoryRepository.findAll()
			.stream()
			.map(CategoryDto::from)
			.collect(Collectors.toList());
	}

	public ProductDetailResponseDto getProduct(Long memberId, Long productId) {
		ProductSellerDetaillDto productDetailDto = productRepository.findProductDetailbyId(productId);
		List<String> imageUrls = productImageRepository.findImageUrlsbyPrdcutId(productId);

		if (memberId.equals(null)) {
			return ProductDetailResponseDto.from(imageUrls, productDetailDto, false);
		}

		Boolean isLiked = wishListRepository.existsMemberLikeProduct(memberId, productId);
		return ProductDetailResponseDto.from(imageUrls, productDetailDto, isLiked);
	}
}
