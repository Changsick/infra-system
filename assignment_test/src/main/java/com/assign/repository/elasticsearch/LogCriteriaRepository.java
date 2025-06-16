package com.assign.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Component;

import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.util.CustomUtility;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogCriteriaRepository {

	private final ElasticsearchTemplate elasticsearchTemplate;
	
	// 이건 jpa querydsl 처럼 rdms 쿼리같이 날라가서 추천안한다고 했던거같은데 아닌가.
	public Page<UserLogEntry> logPagingCriteriaQuery(
			Integer userId, 
            String infoType, 
            String resultType, 
            Long start, 
            Long end,
			Pageable pageable
			) {
		CriteriaQuery query = new CriteriaQuery(new Criteria());
		
		query.addCriteria(Criteria.where("userId").is(userId));
		query.addCriteria(Criteria.where("createdAt").between(CustomUtility.convertLongToLocalDateTime(start)
				, CustomUtility.convertLongToLocalDateTime(end)));
		
		if(infoType != null) {
			query.addCriteria(Criteria.where("infoType").is(infoType));
		}
		if(resultType != null) {
			query.addCriteria(Criteria.where("resultType").is(resultType));
		}
		
		query.setPageable(pageable);
		SearchHits<UserLogEntry> search = elasticsearchTemplate.search(query, UserLogEntry.class);
		
		return new PageImpl<>(search.getSearchHits().stream()
                .map(hit -> hit.getContent()).toList(), 
                pageable, 
                search.getTotalHits());
	}
	
}
