package com.assign.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.assign.model.elasticsearch.UserLogEntry;

@Repository
public interface LogRepository extends ElasticsearchRepository<UserLogEntry, String> {

}
