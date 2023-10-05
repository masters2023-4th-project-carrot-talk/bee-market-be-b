package com.carrot.market.product.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.carrot.market.product.application.dto.request.ProductCreateServiceRequest;
import com.carrot.market.product.application.dto.request.ProductUpdateServiceRequest;
import com.carrot.market.product.application.dto.response.CategoryDto;
import com.carrot.market.product.application.dto.response.DetailPageServiceDto;
import com.carrot.market.product.application.dto.response.ImageResponse;
import com.carrot.market.product.application.dto.response.LocationDetailDto;
import com.carrot.market.product.application.dto.response.ProductChangeStatusResponse;
import com.carrot.market.product.application.dto.response.ProductCreateServiceResponse;
import com.carrot.market.product.application.dto.response.ProductDetailDto;
import com.carrot.market.product.application.dto.response.ProductDetailResponseDto;
import com.carrot.market.product.application.dto.response.ProductUpdateWishList;
import com.carrot.market.product.application.dto.response.SellerDetailDto;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;
import com.carrot.market.product.presentation.dto.request.ProductChangeStatus;
import com.carrot.market.product.presentation.dto.request.ProductCreateRequest;
import com.carrot.market.product.presentation.dto.request.ProductUpdateRequest;
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
			.andExpect(jsonPath("$.data.products[0].title").value("name"))
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
		LocationDetailDto locationDetailDto = new LocationDetailDto(1L, "역삼1동");
		ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(
			List.of(new ImageResponse(1L, "www.google.com")), june, productDetailDto, locationDetailDto);

		when(productService.getProduct(any(), any())).thenReturn(productDetailResponseDto);

		mockMvc.perform(
				get("/api/products/1").requestAttr("memberId", 1)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.images[0].imageId").value(1))
			.andExpect(jsonPath("$.data.images[0].imageUrl").value("www.google.com"))
			.andExpect(jsonPath("$.data.seller.id").value(1L))
			.andExpect(jsonPath("$.data.seller.nickname").value("June"))
			.andExpect(jsonPath("$.data.product.status").value(SellingStatus.SELLING.name()))
			.andExpect(jsonPath("$.data.product.title").value("title"))
			.andExpect(jsonPath("$.data.product.category").value("category"))
			.andExpect(jsonPath("$.data.product.content").value("content"))
			.andExpect(jsonPath("$.data.product.chatCount").value(1L))
			.andExpect(jsonPath("$.data.product.likeCount").value(2L))
			.andExpect(jsonPath("$.data.product.hits").value(3L))
			.andExpect(jsonPath("$.data.product.price").value(1000L))
			.andExpect(jsonPath("$.data.product.isLiked").value(false))
			.andExpect(jsonPath("$.data.location.id").value(1L))
			.andExpect(jsonPath("$.data.location.name").value("역삼1동"));
	}

	@DisplayName("상품을 등록한다.")
	@Test
	public void createProduct() throws Exception {
		//given
		var productCreateRequest = ProductCreateRequest.builder()
			.images(List.of(1L, 2L, 3L))
			.locationId(1L)
			.categoryId(1L)
			.price(3000L)
			.content("내용")
			.title("제목")
			.build();

		var response = ProductCreateServiceResponse.builder().build();

		given(productService.createProduct(anyLong(), any(ProductCreateServiceRequest.class)))
			.willReturn(response);

		//when //then
		mockMvc.perform(post("/api/products")
				.requestAttr("memberId", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productCreateRequest)))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@DisplayName("상품을 수정한다.")
	@Test
	void updateProduct() throws Exception {
		//given
		var productUpdateRequest = ProductUpdateRequest.builder()
			.images(List.of(1L, 2L, 3L))
			.locationId(1L)
			.categoryId(1L)
			.price(3000L)
			.content("내용")
			.title("제목")
			.build();

		//when
		doNothing().when(productService).updateProduct(anyLong(), anyLong(), any(ProductUpdateServiceRequest.class));

		//then
		mockMvc.perform(patch("/api/products/1")
				.requestAttr("memberId", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productUpdateRequest)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("상품 상태를 수정한다.")
	@Test
	public void changeProductStatus() throws Exception {
		//given
		var response = new ProductChangeStatusResponse(1L);
		var productChangeStatus = new ProductChangeStatus("판매완료");
		given(productService.changeProductStatus(anyLong(), anyLong(), anyString()))
			.willReturn(response);

		//when //then
		mockMvc.perform(patch("/api/products/1/status")
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr("memberId", 1)
				.content(objectMapper.writeValueAsString(productChangeStatus)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.productId").isNumber());
	}

	@DisplayName("상품 관심 목록을 추가한다.")
	@Test
	public void updateProductWishList() throws Exception {
		//given
		given(productService.updateProductWishList(anyLong(), anyLong()))
			.willReturn(new ProductUpdateWishList(Boolean.TRUE));

		//when //then
		mockMvc.perform(patch("/api/products/1/like")
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr("memberId", 1))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value("true"))
			.andExpect(jsonPath("$.data.isLiked").value("true"));
	}
}