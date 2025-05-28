package com.assign.repository;

import static com.assign.model.QUserActivityInfoVO.userActivityInfoVO;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.assign.constant.UserConstant.ResultEnum;
import com.assign.constant.UserConstant.UserActivityInfoEnum;
import com.assign.dto.ActivityInfoDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class ActivityInfoRepositoryImpl implements ActivityInfoRepositoryCustom {
	
	private final JPAQueryFactory queryFactory;

	public ActivityInfoRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	@Override
	public Page<ActivityInfoDTO> findInfoData(Long userId, UserActivityInfoEnum infoType, ResultEnum resultType,
			Date startDate, Date endDate, Pageable pageable) {
		JPQLQuery<ActivityInfoDTO> query = 
				queryFactory
					.select(
							Projections.bean(ActivityInfoDTO.class, 
									userActivityInfoVO.infoId
									, userActivityInfoVO.infoType
									, userActivityInfoVO.resultType
									, userActivityInfoVO.reason
									, userActivityInfoVO.userId
									, userActivityInfoVO.loginToken
									, userActivityInfoVO.createdAt
									)
							)
					.from(userActivityInfoVO)
					.where(userActivityInfoVO.userId.eq(userId))
					.where(userActivityInfoVO.createdAt.between(startDate, endDate))
					.where(eqInfoType(infoType))
					.where(eqResultType(resultType));
		query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
		
		List<ActivityInfoDTO> content = query.fetch();
		return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
	}
	
	private BooleanExpression eqInfoType(UserActivityInfoEnum infoType) {
		if(infoType == null) return null;
		return userActivityInfoVO.infoType.eq(infoType);
	}
	
	private BooleanExpression eqResultType(ResultEnum resultType) {
		if(resultType == null) return null;
		return userActivityInfoVO.resultType.eq(resultType);
	}

}
