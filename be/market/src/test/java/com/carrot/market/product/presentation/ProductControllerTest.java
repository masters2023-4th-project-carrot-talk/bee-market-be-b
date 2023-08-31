package com.carrot.market.product.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.carrot.market.product.application.dto.response.MainPageServiceDto;
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;
import com.carrot.market.support.ControllerTestSupport;

class ProductControllerTest extends ControllerTestSupport {

	@Test
	void mainpage() throws Exception {
		// given
		MainPageSliceDto mainPageSliceDto = new MainPageSliceDto(1L, 1L, "name", "location", "image",
			LocalDateTime.now(), 3000L,
			SellingStatus.SELLING.name(), 2L, 2L);
		when(productService.getMainPage(any(), any(), any(), anyInt())).thenReturn(
			new MainPageServiceDto(List.of(mainPageSliceDto), 2L)
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
}