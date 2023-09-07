package com.carrot.market.product.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ProductDetailDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.SellerDetailDto;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;
import com.carrot.market.support.ControllerTestSupport;

class ProductControllerTest extends ControllerTestSupport {

	@Test
	void mainpage() throws Exception {
		// given
		DetailPageSliceResponseDto mainPageSliceDto = new DetailPageSliceResponseDto(1L, 1L, "name", "location",
			"image",
			LocalDateTime.now(), 3000L,
			SellingStatus.SELLING.name(), 2L, 2L);
		when(productService.getMainPage(any(), any(), any(), anyInt())).thenReturn(
			new DetailPageServiceDto(List.of(mainPageSliceDto), 2L)
		);

		// when & then
		mockMvc.perform(
				get("/api/products")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.products[0].id").value(1L))
			.andExpect(jsonPath("$.data.products[0].sellerId").value(1L))
			.andExpect(jsonPath("$.data.products[0].name").value("name"))
			.andExpect(jsonPath("$.data.products[0].location").value("location"))
			.andExpect(jsonPath("$.data.products[0].price").value(3000L))
			.andExpect(jsonPath("$.data.products[0].imageUrl").value("image"))
			.andExpect(jsonPath("$.data.products[0].likeCount").value(2L))
			.andExpect(jsonPath("$.data.products[0].chatCount").value(2L));
	}

	@Test
	void categories() throws Exception {
		// given
		CategoryDto categoryDto = new CategoryDto(1L, "bean", "www.naver.com");
		CategoryDto categoryDto2 = new CategoryDto(2L, "june", "www.google.com");

		when(productService.getCategories()).thenReturn(
			List.of(categoryDto, categoryDto2)
		);

		// when & then
		mockMvc.perform(
				get("/api/categories")
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.[0].id").value(1L))
			.andExpect(jsonPath("$.data.[0].name").value("bean"))
			.andExpect(jsonPath("$.data.[0].imageUrl").value("www.naver.com"))
			.andExpect(jsonPath("$.data.[1].id").value(2L))
			.andExpect(jsonPath("$.data.[1].name").value("june"))
			.andExpect(jsonPath("$.data.[1].imageUrl").value("www.google.com"));
	}

	@Test
	void productDetail() throws Exception {
		// given
		SellerDetailDto june = new SellerDetailDto(1L, "June");
		ProductDetailDto productDetailDto = ProductDetailDto.builder()
			.location("soosongdong")
			.status(SellingStatus.SELLING.name())
			.title("title")
			.category("category")
			.content("content")
			.chatCount(1L)
			.likeCount(2L)
			.hits(3L)
			.price(1000L)
			.isLiked(false)
			.build();
		ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(List.of("www.google.com"),
			june, productDetailDto);

		when(productService.getProduct(any(), any())).thenReturn(productDetailResponseDto);

		mockMvc.perform(
				get("/api/products/1").requestAttr("memberId", 1)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.imageUrls[0]").value("www.google.com"))
			.andExpect(jsonPath("$.data.seller.id").value(1L))
			.andExpect(jsonPath("$.data.seller.nickname").value("June"))
			.andExpect(jsonPath("$.data.product.location").value("soosongdong"))
			.andExpect(jsonPath("$.data.product.status").value(SellingStatus.SELLING.name()))
			.andExpect(jsonPath("$.data.product.title").value("title"))
			.andExpect(jsonPath("$.data.product.category").value("category"))
			.andExpect(jsonPath("$.data.product.content").value("content"))
			.andExpect(jsonPath("$.data.product.chatCount").value(1L))
			.andExpect(jsonPath("$.data.product.likeCount").value(2L))
			.andExpect(jsonPath("$.data.product.hits").value(3L))
			.andExpect(jsonPath("$.data.product.price").value(1000L))
			.andExpect(jsonPath("$.data.product.isLiked").value(false));
	}

}