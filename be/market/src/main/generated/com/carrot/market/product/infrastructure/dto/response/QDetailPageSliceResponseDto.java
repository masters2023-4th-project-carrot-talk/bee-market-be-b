package com.carrot.market.product.infrastructure.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.carrot.market.product.infrastructure.dto.response.QDetailPageSliceResponseDto is a Querydsl Projection type for DetailPageSliceResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QDetailPageSliceResponseDto extends ConstructorExpression<DetailPageSliceResponseDto> {

    private static final long serialVersionUID = 1044694674L;

    public QDetailPageSliceResponseDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<Long> sellerId, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> location, com.querydsl.core.types.Expression<String> imageUrl, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt, com.querydsl.core.types.Expression<Long> price, com.querydsl.core.types.Expression<String> status, com.querydsl.core.types.Expression<Long> likeCount, com.querydsl.core.types.Expression<Long> chatCount) {
        super(DetailPageSliceResponseDto.class, new Class<?>[]{long.class, long.class, String.class, String.class, String.class, java.time.LocalDateTime.class, long.class, String.class, long.class, long.class}, id, sellerId, name, location, imageUrl, createdAt, price, status, likeCount, chatCount);
    }

}

