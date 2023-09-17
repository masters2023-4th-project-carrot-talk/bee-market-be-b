package com.carrot.market.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1613876964L;

    public static final QMember member = new QMember("member1");

    public final com.carrot.market.global.domain.QBaseEntity _super = new com.carrot.market.global.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final ListPath<MemberLocation, QMemberLocation> memberLocations = this.<MemberLocation, QMemberLocation>createList("memberLocations", MemberLocation.class, QMemberLocation.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> modifiedAt = createDateTime("modifiedAt", java.time.LocalDateTime.class);

    public final StringPath nickname = createString("nickname");

    public final ListPath<com.carrot.market.product.domain.Product, com.carrot.market.product.domain.QProduct> products = this.<com.carrot.market.product.domain.Product, com.carrot.market.product.domain.QProduct>createList("products", com.carrot.market.product.domain.Product.class, com.carrot.market.product.domain.QProduct.class, PathInits.DIRECT2);

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath socialId = createString("socialId");

    public final ListPath<WishList, QWishList> wishLists = this.<WishList, QWishList>createList("wishLists", WishList.class, QWishList.class, PathInits.DIRECT2);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

