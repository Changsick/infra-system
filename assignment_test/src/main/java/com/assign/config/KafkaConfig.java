package com.assign.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

//@EnableKafka
public class KafkaConfig {

//	@Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
//        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // 멱등성 활성화
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");  // 모든 복제본에 기록 후 응답
        producerProps.put(ProducerConfig.RETRIES_CONFIG, 3);  // 재시도 횟수 설정
        producerProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);  // 연결 당 최대 요청 수

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        return new KafkaTemplate<>(producerFactory);
    }
    
    /**
     * 
     * 
     * 
     * kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: foo
      auto-offset-reset: earliest
      value-deserializer: org.springframework.kafka.core.JsonDeserializer
      properties:
        spring.deserializer.value: org.springframework.kafka.core.JsonDeserializer
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.core.JsonSerializer
      acks: all
      retries: 3
      enable-idempotence: true
     * 
     */
	
}
