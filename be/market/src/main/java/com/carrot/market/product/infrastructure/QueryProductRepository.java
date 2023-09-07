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
import com.carrot.market.product.domain.SellingStatus;
import com.carrot.market.product.infrastructure.dto.request.DetailPageSliceRequestDto;
import com.carrot.market.product.infrastructure.dto.response.DetailPageSliceResponseDto;
import com.carrot.market.product.infrastructure.dto.response.QDetailPageSliceResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class QueryProductRepository {
	public static final Long BASIC_LOCATION_ID = 1L;
	private final JPQLQueryFactory queryFactory;

	public Slice<DetailPageSliceResponseDto> findByDetailPageSliceRequestDto(
		DetailPageSliceRequestDto detailPageSliceRequestDto) {
		List<DetailPageSliceResponseDto> fetch = queryFactory
			.select(new QDetailPageSliceResponseDto(
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
				lessThanItemId(detailPageSliceRequestDto.nextProductId()),
				locationIdEq(detailPageSliceRequestDto.locationId()),
				categoryEq(detailPageSliceRequestDto.categoryId())
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
			.limit(detailPageSliceRequestDto.pageSize() + 1)
			.fetch();

		JPQLQuery<Product> count = queryFactory
			.selectFrom(product)
			.where(
				locationIdEq(detailPageSliceRequestDto.locationId()),
				categoryEq(detailPageSliceRequestDto.categoryId())
			);

		return PageableExecutionUtils.getPage(fetch, Pageable.ofSize(detailPageSliceRequestDto.pageSize()),
			() -> count.fetchCount());

	}

	public Slice<DetailPageSliceResponseDto> findByMyDetailPageSliceRequestDto(
		DetailPageSliceRequestDto detailPageSliceRequestDto) {
		List<DetailPageSliceResponseDto> fetch = queryFactory
			.select(new QDetailPageSliceResponseDto(
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
				lessThanItemId(detailPageSliceRequestDto.nextProductId()),
				statusEq(detailPageSliceRequestDto.status()),
				categoryEq(detailPageSliceRequestDto.categoryId()),
				sellerdEq(detailPageSliceRequestDto.sellerId()),
				wishListMemberEq(detailPageSliceRequestDto.wishMemberId())

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
			.limit(detailPageSliceRequestDto.pageSize() + 1)
			.fetch();

		JPQLQuery<Product> count = queryFactory
			.selectFrom(product)
			.where(
				categoryEq(detailPageSliceRequestDto.categoryId()),
				statusEq(detailPageSliceRequestDto.status()),
				sellerdEq(detailPageSliceRequestDto.sellerId()),
				wishListMemberEq(detailPageSliceRequestDto.wishMemberId())
			)
			.leftJoin(product.wishLists, wishList);

		return PageableExecutionUtils.getPage(fetch, Pageable.ofSize(detailPageSliceRequestDto.pageSize()),
			() -> count.fetchCount());
	}

	private BooleanExpression lessThanItemId(Long itemId) {
		if (itemId == null) {
			return null;
		}

		return product.id.loe(itemId);
	}

	private BooleanExpression statusEq(String status) {
		if (status == null) {
			return null;
		}
		return product.status.eq(SellingStatus.valueOf(status));

	}

	private BooleanExpression locationIdEq(Long locationId) {
		if (locationId == null) {
			return product.location.id.eq(BASIC_LOCATION_ID);
		}
		return product.location.id.eq(locationId);
	}

	private BooleanExpression sellerdEq(Long memberId) {
		if (memberId == null) {
			return null;
		}
		return product.seller.id.eq(memberId);
	}

	private BooleanExpression wishListMemberEq(Long memberId) {
		if (memberId == null) {
			return null;
		}
		return wishList.member.id.eq(memberId);
	}

	private BooleanExpression categoryEq(Long categoryId) {
		if (categoryId == null) {
			return null;
		}
		return product.category.id.eq(categoryId);
	}
}
