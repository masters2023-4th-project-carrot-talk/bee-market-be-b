package com.carrot.market.product.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.resolver.MemberId;
import com.carrot.market.product.application.ProductService;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.WishListDetailDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ProductController {
	private final ProductService productService;

	@GetMapping("/products")
	public ApiResponse<DetailPageServiceDto> mainPage(
		@RequestParam(required = false) Long locationId,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long next,
		@RequestParam(required = false, defaultValue = "10") int size
	) {
		DetailPageServiceDto mainPage = productService.getMainPage(locationId, categoryId, next, size);
		return ApiResponse.success(mainPage);
	}

	@GetMapping("/categories")
	public ApiResponse<List<CategoryDto>> categories() {
		List<CategoryDto> categories = productService.getCategories();
		return ApiResponse.success(categories);
	}

	@GetMapping("/products/{productId}")
	public ApiResponse<ProductDetailResponseDto> detailProduct(
		@PathVariable Long productId,
		@Login MemberId memberId
	) {
		ProductDetailResponseDto productDetailResponseDto = productService.getProduct(
			memberId.getMemberID(), productId);
		return ApiResponse.success(productDetailResponseDto);
	}

	@GetMapping("/products/sales")
	public ApiResponse<DetailPageServiceDto> sellingPage(
		@RequestParam(required = false) String status,
		@RequestParam(required = false) Long next,
		@RequestParam(required = false, defaultValue = "10") int size,
		@Login MemberId memberId
	) {
		DetailPageServiceDto sellingProducts = productService.getSellingProducts(status, memberId.getMemberID(), next,
			size);
		return ApiResponse.success(sellingProducts);
	}

	@GetMapping("/products/likes")
	public ApiResponse<WishListDetailDto> wishListPage(
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long next,
		@RequestParam(required = false, defaultValue = "10") int size,
		@Login MemberId memberId
	) {
		WishListDetailDto wishList = productService.getWishList(categoryId, memberId.getMemberID(), next, size);
		return ApiResponse.success(wishList);
	}
}
