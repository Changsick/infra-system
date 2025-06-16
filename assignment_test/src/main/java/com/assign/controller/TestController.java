package com.assign.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.assign.dto.response.ResponseDataDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

	
	@PostMapping("")
	public ResponseEntity<ResponseDataDTO<Void>> upload(@RequestParam("file") MultipartFile file){
		return new ResponseEntity<>(ResponseDataDTO.<Void>builder()
                .build(), HttpStatus.OK);
	}
	
	
}
