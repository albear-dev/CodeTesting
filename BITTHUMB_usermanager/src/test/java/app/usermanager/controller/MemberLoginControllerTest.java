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

import app.usermanager.dto.MemberLoginReqDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberLoginControllerTest {
	@Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper = new ObjectMapper();
    
    final MemberLoginReqDto req1 = MemberLoginReqDto.builder()
    								.userId("test_id@test.com")
    								.userPw("1!!!aaadsafafasdfasdf2")
    								.build();
    								
    @Before
    public void setup() throws Exception {
    }

    @Test
    // CASE 001. 유저 정상 로그인
    public void case001() throws Exception {
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.resultMessage").value("성공"))
		                    .andExpect(jsonPath("$.data.token").isNotEmpty())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
    	
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }
    
    @Test
    // CASE 002. 아이디 누락
    public void case002() throws Exception {
    	
    	//given
    	req1.setUserId(null);
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isBadRequest())	// 400
		                    .andExpect(jsonPath("$.httpStatus").value("400"))
		                    .andExpect(jsonPath("$.resultMessage").value("입력값 오류!"))
		                    .andExpect(jsonPath("$.errors").isArray())
		                    .andExpect(jsonPath("$.errors[0].field").value("userId"))
		                    .andExpect(jsonPath("$.errors[0].bizErrorCode").value("common.E002"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }
    
    
    @Test
    // CASE 003. 비밀번호 누락
    public void case003() throws Exception {
    	
    	//given
    	req1.setUserPw(null);
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
			    			.contentType(MediaType.APPLICATION_JSON).content(param))
			                .andExpect(status().isBadRequest())	// 400
			                .andExpect(jsonPath("$.httpStatus").value("400"))
			                .andExpect(jsonPath("$.errors[0].field").value("userPw"))
			                .andExpect(jsonPath("$.errors[0].bizErrorCode").value("common.E002"))
			                .andDo(print())
			    			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }
    
    
    @Test
    // CASE 004. 비밀번호 틀림
    public void case004() throws Exception {
    	
    	//given
    	req1.setUserPw("xxxxxx");
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
			    			.contentType(MediaType.APPLICATION_JSON).content(param))
			                .andExpect(status().isOk())
			                .andExpect(jsonPath("$.httpStatus").value("200"))
			                .andExpect(jsonPath("$.errors[0].bizErrorCode").value("login.E002"))
			                .andDo(print())
			    			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        log.info(stringResult);
    }
     
    
    @Test
    // CASE 005. 잘못된 유저(없는 유저)
    public void case005() throws Exception {
    	
    	//given
    	// 이미 등록되어 있는 아이디
    	req1.setUserId("xxxxx@test.com");
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/member/login")
			    			.contentType(MediaType.APPLICATION_JSON).content(param))
			                .andExpect(status().isOk())
			                .andExpect(jsonPath("$.httpStatus").value("200"))
			                .andExpect(jsonPath("$.errors[0].bizErrorCode").value("login.E001"))
			                .andDo(print())
			    			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        log.info("005.stringResult >>> " + stringResult);
    }

}
