package com.assign.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.assign.dto.KafkaRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	
	private final ObjectMapper mapper;
	
	public void sendMessage(KafkaRequestDTO request) throws JsonProcessingException {
		kafkaTemplate.send(request.getTopic(), mapper.writeValueAsString(request.getPayload()));
	}
}
