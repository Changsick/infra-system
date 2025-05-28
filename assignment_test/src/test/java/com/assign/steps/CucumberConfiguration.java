package com.assign.steps;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.assign.AssignmentTestApplication;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = AssignmentTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberConfiguration {

}
