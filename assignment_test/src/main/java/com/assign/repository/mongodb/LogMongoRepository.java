package com.assign.repository.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.assign.model.mongodb.UserLogMongo;

@Repository
public interface LogMongoRepository extends ReactiveMongoRepository<UserLogMongo, String> {

}
