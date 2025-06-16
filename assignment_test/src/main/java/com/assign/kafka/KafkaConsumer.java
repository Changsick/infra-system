package com.assign.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.assign.model.elasticsearch.UserLogEntry;
import com.assign.model.mongodb.UserLogMongo;
import com.assign.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
	
	private final ObjectMapper mapper;
	
	private final UserService userService;

	@KafkaListener(topics = "log-elastic-topic", groupId = "foo")
	public void logElasticTopicConsumer(String message) throws JsonMappingException, JsonProcessingException {
		UserLogEntry logEntry = mapper.readValue(message, UserLogEntry.class);
		log.info("### logElasticTopicConsumer meessage : " + logEntry);
		userService.saveUserLogElasticsearch(logEntry);
		
	}
	
	@KafkaListener(topics = "log-mongo-topic", groupId = "foo")
	public void logMongoTopicConsumer(String message) throws JsonMappingException, JsonProcessingException {
		UserLogMongo logMongo = mapper.readValue(message, UserLogMongo.class);
		log.info("### logMongoTopicConsumer meessage : " + logMongo);
		userService.saveUserLogMongo(logMongo);
	}
	
}
