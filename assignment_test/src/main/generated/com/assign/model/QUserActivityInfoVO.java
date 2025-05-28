package com.assign.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserActivityInfoVO is a Querydsl query type for UserActivityInfoVO
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserActivityInfoVO extends EntityPathBase<UserActivityInfoVO> {

    private static final long serialVersionUID = -827049160L;

    public static final QUserActivityInfoVO userActivityInfoVO = new QUserActivityInfoVO("userActivityInfoVO");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final NumberPath<Long> infoId = createNumber("infoId", Long.class);

    public final EnumPath<com.assign.constant.UserConstant.UserActivityInfoEnum> infoType = createEnum("infoType", com.assign.constant.UserConstant.UserActivityInfoEnum.class);

    public final StringPath loginToken = createString("loginToken");

    public final StringPath reason = createString("reason");

    public final EnumPath<com.assign.constant.UserConstant.ResultEnum> resultType = createEnum("resultType", com.assign.constant.UserConstant.ResultEnum.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserActivityInfoVO(String variable) {
        super(UserActivityInfoVO.class, forVariable(variable));
    }

    public QUserActivityInfoVO(Path<? extends UserActivityInfoVO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserActivityInfoVO(PathMetadata metadata) {
        super(UserActivityInfoVO.class, metadata);
    }

}

