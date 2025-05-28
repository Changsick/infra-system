package com.assign.model;

import com.assign.dto.request.CsvDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_tb")
@Data
@NoArgsConstructor
public class TestTbVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_pk")
	private Long testPk;
	
	@Column(name = "test_col1")
	private Integer testCol1;
	
	@Column(name = "test_col2")
	private String testCol2;
	
	@Column(name = "test_col3")
	private String testCol3;

	@Builder(builderClassName = "setAllArgsBuilder", builderMethodName = "setAllArgsBuilder")
	public TestTbVO(Long testPk, Integer testCol1, String testCol2, String testCol3) {
		this.testPk = testPk;
		this.testCol1 = testCol1;
		this.testCol2 = testCol2;
		this.testCol3 = testCol3;
	}
	
	public static TestTbVO from(CsvDTO dto) {
	    return TestTbVO.setAllArgsBuilder()
	            .testCol1(dto.getCcc())
	            .testCol2(dto.getAaa())
	            .testCol3(dto.getBbb())
	            .build();
	}
	
}
