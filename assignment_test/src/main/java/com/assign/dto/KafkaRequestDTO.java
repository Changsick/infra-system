package com.assign.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaRequestDTO {

	private String topic;
	private Object payload;
}
