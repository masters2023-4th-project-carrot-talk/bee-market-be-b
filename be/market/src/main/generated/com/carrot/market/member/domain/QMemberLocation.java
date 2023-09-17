package com.carrot.market.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberLocation is a Querydsl query type for MemberLocation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberLocation extends EntityPathBase<MemberLocation> {

    private static final long serialVersionUID = 849391289L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberLocation memberLocation = new QMemberLocation("memberLocation");

    public final com.carrot.market.global.domain.QBaseEntity _super = new com.carrot.market.global.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isMain = createBoolean("isMain");

    public final com.carrot.market.location.domain.QLocation location;

    public final QMember member;

    public QMemberLocation(String variable) {
        this(MemberLocation.class, forVariable(variable), INITS);
    }

    public QMemberLocation(Path<? extends MemberLocation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberLocation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberLocation(PathMetadata metadata, PathInits inits) {
        this(MemberLocation.class, metadata, inits);
    }

    public QMemberLocation(Class<? extends MemberLocation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.location = inits.isInitialized("location") ? new com.carrot.market.location.domain.QLocation(forProperty("location")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

