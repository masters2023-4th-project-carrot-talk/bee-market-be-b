package com.carrot.market.product.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1293745048L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final com.carrot.market.global.domain.QBaseEntity _super = new com.carrot.market.global.domain.QBaseEntity(this);

    public final QCategory category;

    public final ListPath<com.carrot.market.chatroom.domain.Chatroom, com.carrot.market.chatroom.domain.QChatroom> chatrooms = this.<com.carrot.market.chatroom.domain.Chatroom, com.carrot.market.chatroom.domain.QChatroom>createList("chatrooms", com.carrot.market.chatroom.domain.Chatroom.class, com.carrot.market.chatroom.domain.QChatroom.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.carrot.market.location.domain.QLocation location;

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final QProductDetails productDetails;

    public final ListPath<ProductImage, QProductImage> productImages = this.<ProductImage, QProductImage>createList("productImages", ProductImage.class, QProductImage.class, PathInits.DIRECT2);

    public final com.carrot.market.member.domain.QMember seller;

    public final EnumPath<SellingStatus> status = createEnum("status", SellingStatus.class);

    public final ListPath<com.carrot.market.member.domain.WishList, com.carrot.market.member.domain.QWishList> wishLists = this.<com.carrot.market.member.domain.WishList, com.carrot.market.member.domain.QWishList>createList("wishLists", com.carrot.market.member.domain.WishList.class, com.carrot.market.member.domain.QWishList.class, PathInits.DIRECT2);

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.location = inits.isInitialized("location") ? new com.carrot.market.location.domain.QLocation(forProperty("location")) : null;
        this.productDetails = inits.isInitialized("productDetails") ? new QProductDetails(forProperty("productDetails")) : null;
        this.seller = inits.isInitialized("seller") ? new com.carrot.market.member.domain.QMember(forProperty("seller")) : null;
    }

}

