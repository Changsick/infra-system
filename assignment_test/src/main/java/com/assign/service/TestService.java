package com.assign.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.assign.dto.request.CsvDTO;
import com.assign.model.TestTbVO;
import com.assign.repository.TestRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
	
	private final TestRepository testRepository;

	public void parseCsv(MultipartFile file) throws IOException {
		try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // CsvToBean을 사용하여 record DTO로 매핑
            CsvToBean<CsvDTO> csvToBean = new CsvToBeanBuilder<CsvDTO>(reader)
                    .withType(CsvDTO.class) // YourCsvDTO로 매핑
                    .withIgnoreLeadingWhiteSpace(true) // 공백 무시
                    .build();

            List<CsvDTO> csvRecords = csvToBean.parse();
            List<TestTbVO> list = csvRecords.stream().map(TestTbVO::from).collect(Collectors.toList());
            // DB에 저장

            testRepository.saveAll(list); // DB에 저장
        }
	}
	
}
