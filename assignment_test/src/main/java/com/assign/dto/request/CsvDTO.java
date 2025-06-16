package com.assign.dto.request;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class CsvDTO {

	@CsvBindByName(column = "aaa")
	private String aaa;
	
	@CsvBindByName(column = "bbb")
	private String bbb;
	
	@CsvBindByName(column = "ccc")
	private Integer ccc;
	
	
}
