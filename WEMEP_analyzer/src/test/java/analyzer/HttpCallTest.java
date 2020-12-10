package analyzer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import analyzer.app.AppMain;
import analyzer.app.dto.AnalyzeReqDto;

@SpringBootTest(classes = AppMain.class)
@AutoConfigureMockMvc
public class HttpCallTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void URL_조회() throws Exception {
		
		//given
		AnalyzeReqDto analyzeReqDto = new AnalyzeReqDto();
		analyzeReqDto.setUrl("http://google.com");
		analyzeReqDto.setType(2);
		analyzeReqDto.setUnit(30);
		String param = objectMapper.writeValueAsString(analyzeReqDto);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/analyze")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.code").value("200"))
		                    .andExpect(jsonPath("$.data.cut").isNotEmpty())
		                    .andExpect(jsonPath("$.data.remainder").isNotEmpty())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
	}
	
	
	@Test
	public void 잘못된_요청() throws Exception {
		
		//given
		AnalyzeReqDto analyzeReqDto = new AnalyzeReqDto();
		// 요청값 비어 있음
		String param = objectMapper.writeValueAsString(analyzeReqDto);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/analyze")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().is4xxClientError())
		                    .andExpect(jsonPath("$.code").value("400"))
		                    .andExpect(jsonPath("$.message").isNotEmpty())
		                    .andExpect(jsonPath("$.errors").isNotEmpty())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
	}
}
