package com.assign.repository.mongodb;

import java.util.Collections;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.assign.model.mongodb.UserLogMongo;
import com.assign.util.CustomUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogMongoCriteriaRepository {

	private final ReactiveMongoTemplate mongoTemplate;
	
	// 이건 jpa querydsl 처럼 rdms 쿼리같이 날라가서 추천안한다고 했던거같은데 아닌가.
	public Mono<Page<UserLogMongo>> logPagingCriteriaQuery(
			Integer userId, 
            String infoType, 
            String resultType, 
            Long start, 
            Long end,
			Pageable pageable
			) {
		
		Query query = new Query();
		
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("createdAt")
				.gte(CustomUtility.convertLongToLocalDateTime(start))
				.lte(CustomUtility.convertLongToLocalDateTime(end)));
		
		if(infoType != null) {
			query.addCriteria(Criteria.where("infoType").is(infoType));
		}
		if(resultType != null) {
			query.addCriteria(Criteria.where("resultType").is(resultType));
		}
		
		Mono<Long> totalMono = mongoTemplate.count(query, UserLogMongo.class);
		
		// 🔹 정렬 및 페이징 적용
        query.with(pageable.getSort());
        query.with(pageable);
        
        Flux<UserLogMongo> logFlux = mongoTemplate.find(query, UserLogMongo.class);
        Mono<Page<UserLogMongo>> result = totalMono.flatMap(total -> 
			logFlux.collectList()
			.map(users -> new PageImpl<>(users, pageable, total))
			.switchIfEmpty(Mono.just(new PageImpl<>(Collections.emptyList(), pageable, 0)))
		);
        
        return result;
	}
	
}
