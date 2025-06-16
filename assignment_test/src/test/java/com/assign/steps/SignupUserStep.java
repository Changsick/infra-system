package com.assign.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.assign.dto.UserDTO;
import com.assign.dto.request.SignupRequestDTO;
import com.assign.dto.response.ResponseDataDTO;
import com.assign.model.UserVO;
import com.assign.repository.UserRepository;
import com.assign.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SignupUserStep {

	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;

    private SignupRequestDTO signRequestDTO;
    
    private ResultActions response;
    
    @Given("이메일 {string}, 비밀번호 {string}, 사용자명 {string}인 회원이 있다")
    public void initSignUpData(String email, String password, String username) {
    	signRequestDTO = SignupRequestDTO.builder()
    			.email(email)
    			.password(password)
    			.username(username)
    			.build();
    }

    @When("회원이 {string}에 POST 요청을 보낸다")
    public void sendRequestSignUp(String url) throws Exception {
        response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequestDTO)));
    }

    @Then("응답 상태는 {int} Created여야 한다")
    public void checkStatusCode(int statusCode) throws Exception {
        response.andExpect(status().is(statusCode));
    }
    
    @Then("리턴된 회원 정보는 이메일 {string}, 사용자명 {string}을 포함해야 한다")
    public void checkReturnData(String email, String username) throws Exception {
    	String jsonResponse = response.andReturn().getResponse().getContentAsString();
    	ResponseDataDTO<UserDTO> userResponse = objectMapper.readValue(
    			jsonResponse, new TypeReference<ResponseDataDTO<UserDTO>>() {});
    	
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getData().getEmail()).isEqualTo(email);
        assertThat(userResponse.getData().getUsername()).isEqualTo(username);
        assertThat(userResponse.getData().getCreatedAt()).isNotNull();
    }

    @Then("회원 정보가 데이터베이스에 저장되어야 한다")
    public void checkDbData() {
        Optional<UserVO> savedUser = userRepository.findByEmail(signRequestDTO.getEmail());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo(signRequestDTO.getUsername());
    }
    
    @Given("가입되어있는 이메일 {string}, 비밀번호 {string}, 사용자명 {string}인 회원이 있다")
    public void initSignUpFailData(String email, String password, String username) {
    	signRequestDTO = SignupRequestDTO.builder()
    			.email(email)
    			.password(password)
    			.username(username)
    			.build();
    }

    @When("회원이 {string}에 POST 실패할 요청을 보낸다")
    public void sendRequestSignUpFail(String url) throws Exception {
        response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequestDTO)));
    }

    @Then("응답 상태는 {int} Bad Request여야 한다")
    public void checkStatusFailureCode(int statusCode) throws Exception {
        response.andExpect(status().is(statusCode));
    }
	
}
