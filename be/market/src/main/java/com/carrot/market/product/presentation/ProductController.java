package com.carrot.market.product.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrot.market.global.presentation.ApiResponse;
import com.carrot.market.product.application.ProductService;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.MainPageServiceDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ProductController {
	private final ProductService productService;

	@GetMapping("/products")
	public ApiResponse<MainPageServiceDto> mainPage(
		@RequestParam(required = false) Long locationId,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long next,
		@RequestParam(required = false, defaultValue = "10") int size
	) {
		MainPageServiceDto mainPage = productService.getMainPage(locationId, categoryId, next, size);
		return ApiResponse.success(mainPage);
	}

	@GetMapping("/categories")
	public ApiResponse<List<CategoryDto>> categories() {
		List<CategoryDto> categories = productService.getCategories();
		return ApiResponse.success(categories);
	}
}
