package com.assign.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.assign.dto.TokenInfoDTO;
import com.assign.dto.request.SignRequestDTO;
import com.assign.dto.response.ResponseDataDTO;
import com.assign.model.UserActivityInfoVO;
import com.assign.repository.UserActivityInfoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginUserStep {
	
	@Autowired
	UserActivityInfoRepository activityRepository;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;

    private SignRequestDTO signRequestDTO;
    
    private ResultActions response;
    
    private static final Map<String, String> context = new HashMap<>();
    
    @Given("사용자가 {string} 이메일과 {string} 비밀번호로 로그인 시도")
    public void initLoginData(String email, String password) {
    	signRequestDTO = SignRequestDTO.builder()
    			.email(email)
    			.password(password)
    			.build();
    }

    @When("사용자가 {string} 엔드포인트에 POST 요청을 보낸다")
    public void sendRequestLogin(String url) throws Exception {
        response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequestDTO)));
    }

    @Then("응답 상태는 {int}이어야 한다")
    public void checkLoginStatusCode(int statusCode) throws Exception {
        response.andExpect(status().is(statusCode));
    }
    
    @Then("응답 데이터에 {string}, {string}, {string} 필드가 포함되어 있어야 한다")
    public void checkLoginReturnData(String token, String iat, String exp) throws Exception {
    	String jsonResponse = response.andReturn().getResponse().getContentAsString();
    	ResponseDataDTO<TokenInfoDTO> userResponse = objectMapper.readValue(
    			jsonResponse, new TypeReference<ResponseDataDTO<TokenInfoDTO>>() {});
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getData().getToken()).isNotNull();
        assertThat(userResponse.getData().getIat()).isNotNull();
        assertThat(userResponse.getData().getExp()).isNotNull();
        
        context.put("loginToken", userResponse.getData().getToken());
    }

    @Then("로그인 이력이 저장되어야 한다")
    public void checkLoginDbData() {
    	String loginToken = context.get("loginToken");
    	Optional<UserActivityInfoVO> loginInfo = activityRepository.findByLoginToken(loginToken);
        assertThat(loginInfo).isPresent();
        assertThat(loginInfo.get().getLoginToken()).isEqualTo(loginToken);
    }
    
	// 
    @Given("로그인 이후 토큰값을 준비한다")
    public void loginAfterToken() {
    	// loginToken
    }

    @When("jwt토큰을 헤더에 넣고 {string} 으로 요청")
    public void sendRequestLogout(String url) throws Exception {
        response = mockMvc.perform(post(url)
        		.header("Authorization", context.get("loginToken"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequestDTO)));
    }

    @Then("로그아웃 응답 상태는 {int}이어야 한다")
    public void checkLogoutStatusCode(int statusCode) throws Exception {
        response.andExpect(status().is(statusCode));
    }
    
    @Then("응답 데이터에 {string} 값은 {string} 로 나온다")
    public void checkLogoutReturnData(String key, String expectedValue) throws Exception {
    	String jsonResponse = response.andReturn().getResponse().getContentAsString();
    	ResponseDataDTO<Map> userResponse = objectMapper.readValue(
    			jsonResponse, new TypeReference<ResponseDataDTO<Map>>() {});
        assertThat(userResponse).isNotNull();
        assertThat(String.valueOf(userResponse.getData().get(key))).isEqualTo(expectedValue);
        
    }
}
