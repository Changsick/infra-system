package com.assign.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.assign.repository.mongodb")
public class MongoConfig {

	@Value("${spring.data.mongodb.uri}")
	String host;
	
	@Value("${spring.data.mongodb.database}")
	String databaseName;
	
	@Bean
    public MongoClient mongoClient() {
        // MongoDB 연결을 위한 MongoClient 생성
        return MongoClients.create(host);
    }
    
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoCustomConversions conversions, 
                                                       MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(
                NoOpDbRefResolver.INSTANCE, context);
        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null)); // _class 필드 제거
        return converter;
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MappingMongoConverter mappingMongoConverter) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient(), databaseName), mappingMongoConverter);
    }
    
}
