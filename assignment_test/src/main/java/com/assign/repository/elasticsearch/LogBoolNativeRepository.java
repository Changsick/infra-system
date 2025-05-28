package com.assign.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.util.CustomUtility;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogBoolNativeRepository {

	private final ElasticsearchTemplate elasticsearchTemplate;
	
	// 이걸 더 권장?? 한다고한거같은데..
	public Page<UserLogEntry> logPagingNativeQuery(Integer userId, 
	        String infoType, 
	        String resultType, 
	        Long start, 
	        Long end,
			Pageable pageable) {
		
		BoolQuery.Builder boolQuery = new BoolQuery.Builder();
	    boolQuery.must(m -> m.match(m1 -> m1.field("userId").query(userId)));
	          
	    RangeQuery dateRange = new RangeQuery.Builder().term(t -> t.field("createdAt")
	    		.gte(CustomUtility.convertLongToDateTimeString(start))
	    		.lte(CustomUtility.convertLongToDateTimeString(end))
	    		)
	    .build();
	    
	    boolQuery.filter(f -> f.range(dateRange));
	    
	    if(infoType != null) {
	    	boolQuery.must(m -> m.match(m1 -> m1.field("infoType").query(infoType)));
	    }
	    
	    if(resultType != null) {
	    	boolQuery.must(m -> m.match(m1 -> m1.field("resultType").query(resultType)));
	    }
	    
	    NativeQuery nativeQuery = new NativeQuery(boolQuery.build()._toQuery());
	    nativeQuery.setPageable(pageable);
//	    nativeQuery.setSearchAfter(Arrays.asList(start, "lastId")); // scroll paging을 쓸 때.
	    SearchHits<UserLogEntry> search = elasticsearchTemplate.search(nativeQuery, UserLogEntry.class);
	    
	    return new PageImpl<>(search.getSearchHits().stream()
	            .map(hit -> hit.getContent()).toList(), 
	            pageable, 
	            search.getTotalHits());
	}
	
}
