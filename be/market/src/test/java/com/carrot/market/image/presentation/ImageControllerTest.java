package com.carrot.market.image.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;

import com.carrot.market.image.application.dto.response.ImageResponse;
import com.carrot.market.support.ControllerTestSupport;

class ImageControllerTest extends ControllerTestSupport {
	@DisplayName("이미지를 업로드한다.")
	@Test
	void uploadImage() throws Exception {
		var imageResponses = List.of(
			new ImageResponse(1L, "image1"),
			new ImageResponse(2L, "image2")
		);

		//given
		given(imageService.uploadImages(anyList())).willReturn(imageResponses);

		//when //then
		mockMvc.perform(multipart(HttpMethod.POST, "/api/images")
				.file(new MockMultipartFile("images", new byte[0]))
				.file(new MockMultipartFile("images", new byte[0])))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].imageId").value(1L))
			.andExpect(jsonPath("$.data[0].imageUrl").value("image1"))
			.andExpect(jsonPath("$.data[1].imageId").value(2L))
			.andExpect(jsonPath("$.data[1].imageUrl").value("image2"));
	}
}