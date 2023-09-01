package com.carrot.market.product.application;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrot.market.product.application.dto.response.MainPageServiceDto;
import com.carrot.market.product.infrastructure.QueryProductRepository;
import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {
	private final QueryProductRepository queryProductRepository;

	public MainPageServiceDto getMainPage(Long locationId, Long categoryId, Long next, int size) {
		Slice<MainPageSliceDto> byLocationIdAndCategoryId = queryProductRepository.findByLocationIdAndCategoryId(
			locationId, categoryId, next, size);
		List<MainPageSliceDto> products = byLocationIdAndCategoryId.getContent();
		Long contentNextId = getContentNextId(products, size);
		products = removeLastIfProductsSizeEqualPageSize(products, size);
		return new MainPageServiceDto(products, contentNextId);
	}

	private List<MainPageSliceDto> removeLastIfProductsSizeEqualPageSize(List<MainPageSliceDto> content, int size) {
		if (content.size() == size + 1) {
			return content.subList(0, content.size() - 1);
		}
		return content;
	}

	private Long getContentNextId(List<MainPageSliceDto> content, int pageSize) {
		Long nextContentId = null;
		if (content != null && content.size() == pageSize + 1) {
			nextContentId = content.get(content.size() - 1).getId();
		}
		return nextContentId;
	}
}
