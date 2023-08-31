package com.carrot.market.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.carrot.market.product.application.ProductService;
import com.carrot.market.product.presentation.ProductController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = {
	ProductController.class
})
public abstract class ControllerTestSupport {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected ProductService productService;

}