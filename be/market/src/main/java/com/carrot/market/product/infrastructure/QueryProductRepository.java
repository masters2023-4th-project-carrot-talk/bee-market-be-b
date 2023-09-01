package com.carrot.market.product.infrastructure;

import static com.carrot.market.chatroom.domain.QChatroom.*;
import static com.carrot.market.image.domain.QImage.*;
import static com.carrot.market.location.domain.QLocation.*;
import static com.carrot.market.member.domain.QWishList.*;
import static com.carrot.market.product.domain.QProduct.*;
import static com.carrot.market.product.domain.QProductImage.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.carrot.market.member.domain.QMember;
import com.carrot.market.product.domain.Product;
import com.carrot.market.product.infrastructure.dto.MainPageSliceDto;
import com.carrot.market.product.infrastructure.dto.QMainPageSliceDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class QueryProductRepository {
	private static final Long BASIC_LOCATION_ID = 1L;
	private final JPQLQueryFactory queryFactory;

	public Slice<MainPageSliceDto> findByLocationIdAndCategoryId(Long locationId, Long categoryId,
		Long productId, int pageSize) {
		List<MainPageSliceDto> fetch = queryFactory
			.select(new QMainPageSliceDto(
					product.id,
					product.seller.id,
					product.productDetails.name,
					location.name,
					image.imageUrl,
					product.createdAt,
					product.productDetails.price,
					product.status.stringValue(),
					wishList.countDistinct(),
					chatroom.countDistinct()
				)
			)
			.from(product)
			.where(
				lessThanItemId(productId),
				locationIdEq(locationId),
				categoryEq(categoryId)
			)
			.orderBy(product.id.desc())
			.leftJoin(product.location, location)
			.leftJoin(product.seller, new QMember("m1"))
			.leftJoin(product.productImages, productImage)
			.on(productImage.isMain.eq(true))
			.leftJoin(productImage.image, image)
			.leftJoin(product.wishLists, wishList)
			.leftJoin(product.chatrooms, chatroom)
			.groupBy(product.id)
			.groupBy(product.seller.id)
			.groupBy(product.productDetails.name)
			.groupBy(location.name)
			.groupBy(image.imageUrl)
			.groupBy(product.createdAt)
			.groupBy(product.productDetails.price)
			.groupBy(product.status.stringValue())
			.limit(pageSize + 1)
			.fetch();

		JPQLQuery<Product> count = queryFactory
			.selectFrom(product)
			.where(
				locationIdEq(locationId),
				categoryEq(categoryId)
			);

		return PageableExecutionUtils.getPage(fetch, Pageable.ofSize(pageSize), () -> count.fetchCount());
	}

	private BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return product.id.loe(itemId);
	}

	private BooleanExpression locationIdEq(Long locationId) {
		if (locationId.equals(null)) {
			return product.location.id.eq(BASIC_LOCATION_ID);
		}
		return product.location.id.eq(locationId);
	}

	private BooleanExpression categoryEq(Long categoryId) {
		if (categoryId.equals(null)) {
			return null;
		}
		return product.category.id.eq(categoryId);
	}
}
