package com.assign.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserVO is a Querydsl query type for UserVO
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserVO extends EntityPathBase<UserVO> {

    private static final long serialVersionUID = -1155286789L;

    public static final QUserVO userVO = new QUserVO("userVO");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final StringPath email = createString("email");

    public final DateTimePath<java.util.Date> lastChangePassword = createDateTime("lastChangePassword", java.util.Date.class);

    public final DateTimePath<java.util.Date> lastLogin = createDateTime("lastLogin", java.util.Date.class);

    public final StringPath password = createString("password");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final EnumPath<com.assign.constant.UserConstant.UserLevelEnum> userLevel = createEnum("userLevel", com.assign.constant.UserConstant.UserLevelEnum.class);

    public final StringPath username = createString("username");

    public QUserVO(String variable) {
        super(UserVO.class, forVariable(variable));
    }

    public QUserVO(Path<? extends UserVO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserVO(PathMetadata metadata) {
        super(UserVO.class, metadata);
    }

}

