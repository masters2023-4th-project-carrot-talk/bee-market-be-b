package com.carrot.market.product.presentation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.member.presentation.annotation.Login;
import com.carrot.market.member.resolver.MemberId;
import com.carrot.market.product.application.ProductService;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductChangeStatusResponse;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.ProductUpdateWishList;
import com.carrot.market.product.application.dto.response.WishListDetailDto;
import com.carrot.market.product.presentation.dto.request.ProductChangeStatus;
import com.carrot.market.product.presentation.dto.request.ProductCreateRequest;
import com.carrot.market.product.presentation.dto.request.ProductUpdateRequest;
import com.carrot.market.product.presentation.dto.response.ProductCreateResponse;

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

	@PostMapping("/products")
	public ResponseEntity<ApiResponse<ProductCreateResponse>> createProduct(
		@Login MemberId memberId,
		@RequestBody ProductCreateRequest request
	) {
		var response = productService.createProduct(memberId.getMemberID(),
			request.toProductCreateServiceRequest());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(new ProductCreateResponse(response.id())));
	}

	@PatchMapping("/products/{productId}/status")
	public ApiResponse<ProductChangeStatusResponse> changeProductStatus(
		@PathVariable Long productId,
		@Login MemberId memberId,
		@RequestBody ProductChangeStatus request
	) {
		var response = productService.changeProductStatus(
			memberId.getMemberID(), productId, request.status());

		return ApiResponse.success(response);
	}

	@PatchMapping("/products/{productId}/like")
	public ApiResponse<ProductUpdateWishList> updateProductWishList(
		@PathVariable Long productId,
		@Login MemberId memberId
	) {
		var response = productService.updateProductWishList(memberId.getMemberID(), productId);

		return ApiResponse.success(response);
	}

	@PatchMapping("/products/{productId}")
	public ApiResponse<Void> updateProduct(
		@PathVariable Long productId,
		@Login MemberId memberId,
		@RequestBody ProductUpdateRequest request
	) {
		productService.updateProduct(memberId.getMemberID(), productId,
			request.toProductUpdateServiceRequest());

		return ApiResponse.successNoBody();
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

	@DeleteMapping("/products/{productId}")
	public ApiResponse<Void> removeProduct(
		@PathVariable Long productId,
		@Login MemberId memberId
	) {
		productService.removeProduct(productId, memberId.getMemberID());

		return ApiResponse.successNoBody();
	}
}
