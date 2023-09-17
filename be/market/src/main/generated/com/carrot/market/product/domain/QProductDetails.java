package com.carrot.market.product.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductDetails is a Querydsl query type for ProductDetails
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QProductDetails extends BeanPath<ProductDetails> {

    private static final long serialVersionUID = 807850890L;

    public static final QProductDetails productDetails = new QProductDetails("productDetails");

    public final StringPath content = createString("content");

    public final NumberPath<Long> hits = createNumber("hits", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public QProductDetails(String variable) {
        super(ProductDetails.class, forVariable(variable));
    }

    public QProductDetails(Path<? extends ProductDetails> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductDetails(PathMetadata metadata) {
        super(ProductDetails.class, metadata);
    }

}

