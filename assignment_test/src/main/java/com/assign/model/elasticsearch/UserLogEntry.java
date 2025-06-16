package com.assign.model.elasticsearch;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(indexName = "user_logs", writeTypeHint = WriteTypeHint.FALSE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserLogEntry {

	@Id
	private String id;
	
	@Field(type = FieldType.Text, name = "infoType")
	private String infoType;
	
	@Field(type = FieldType.Text, name = "resultType")
	private String resultType;

	@Field(type = FieldType.Text, name = "reason")
	private String reason;
	
	@Field(type = FieldType.Long, name = "userId")
	private Long userId;
	
	@Field(type = FieldType.Text, name = "loginToken")
	private String loginToken;
	
//	@Field(type = FieldType.Date, name = "createdAt", format = DateFormat.date_time)
//	@Field(type = FieldType.Long, name = "createdAt")
	@Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

}
