package com.assign.model.mongodb;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "user_logs")
public class UserLogMongo {

	private String id;
	
	private String infoType;
	
	private String resultType;

	private String reason;
	
	private Long userId;
	
	private String loginToken;

	private LocalDateTime createdAt;
}
