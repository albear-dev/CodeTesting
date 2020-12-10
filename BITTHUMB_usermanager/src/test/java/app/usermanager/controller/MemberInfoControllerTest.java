package app.usermanager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import app.usermanager.dto.MemberInfoReqDto;
import app.usermanager.dto.MemberLoginReqDto;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberInfoControllerTest {
	@Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String token;
    final MemberInfoReqDto req1 = MemberInfoReqDto.builder()
    								.userId("test_id@test.com")
    								.build();
    								
    @Before
    public void setup() throws Exception {
    	 final MemberLoginReqDto req1 = MemberLoginReqDto.builder()
					.userId("test_id@test.com")
					.userPw("1!!!aaadsafafasdfasdf2")
					.build();
    	 
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
    			.contentType(MediaType.APPLICATION_JSON).content(param))
                .andDo(print())
    			.andReturn();
    	
    	String stringResult = result.getResponse().getContentAsString();
    	JsonObject convertedObject = new Gson().fromJson(stringResult, JsonObject.class);
    	JsonObject dataObj =  (JsonObject)convertedObject.get("data");
		this.token = dataObj.get("token").getAsString();
		System.out.println("this.token1 >> " + this.token);
		
    }

    @Test
    // CASE 001. 유저 정보 정상 조회
    public void case001() throws Exception {
    	System.out.println("this.token2 >> " + this.token);
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/info")
		        			.contentType(MediaType.APPLICATION_JSON).content(param).header("Authorization", "Bearer " + this.token))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.data.userName").isNotEmpty())
		                    .andExpect(jsonPath("$.data.userEmail").isNotEmpty())
		                    .andExpect(jsonPath("$.data.lastLoginDate").isNotEmpty())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
    	
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }
    
    
    @Test
    // CASE 002. 토큰값 오류
    public void case002() throws Exception {
    	System.out.println("this.token2 >> " + this.token);
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/info")
		        			.contentType(MediaType.APPLICATION_JSON).content(param).header("Authorization", "xxxxxxxxxx"))
		                    .andExpect(status().isUnauthorized())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
    	
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }

}
