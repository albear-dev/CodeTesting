package app.boardmanager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.boardmanager.dto.BoardDeleteReqDto;
import app.boardmanager.dto.BoardDetailReqDto;
import app.boardmanager.dto.BoardListReqDto;
import app.boardmanager.dto.BoardModifyReqDto;
import app.boardmanager.dto.BoardRegReqDto;



@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardManagerControllerTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private MockMvc mockMvc;

    private static ObjectMapper objectMapper = new ObjectMapper();
    								
    @Before
    public void setup() throws Exception {
    }

    /**
     * 게시판 리스트 조회
     * 
     * @throws Exception
     */
    @Test
    public void selectListBoard() throws Exception {
    	 final BoardListReqDto req1 = new BoardListReqDto();
    	 req1.setPage(1);
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/list")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.data").isArray())
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    /**
     * 게시판 상세조회
     * 
     * @throws Exception
     */
    @Test
    public void selectDetailBoard() throws Exception {
    	 final BoardDetailReqDto req1 = new BoardDetailReqDto();
    	 req1.setSeq("1");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/detail")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.data.seq").value("1"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    /**
     * 게시판 상세조회(없는 내용)
     * 
     * @throws Exception
     */
    @Test
    public void selectDetailBoardNoContents() throws Exception {
    	 final BoardDetailReqDto req1 = new BoardDetailReqDto();
    	 req1.setSeq("9999999");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/detail")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.errors").isArray())
		                    .andExpect(jsonPath("$.errors[0].bizErrorCode").value("detail.E001"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    
    /**
     * 게시판 등록
     * 
     * @throws Exception
     */
    @Test
    public void insertBoard() throws Exception {
    	 final BoardRegReqDto req1 = new BoardRegReqDto();
    	 req1.setSubject("제목 - " + UUID.randomUUID());
    	 req1.setContent("내용 -" + UUID.randomUUID());
    	 req1.setRegName("김말자");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/insert")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    
    /**
     * 게시판 수정
     * 
     * @throws Exception
     */
    @Test
    public void modifyBoard() throws Exception {
    	 final BoardModifyReqDto req1 = new BoardModifyReqDto();
    	 req1.setSeq("1");
    	 req1.setSubject("제목(수정) - " + UUID.randomUUID());
    	 req1.setContent("내용(수정) -" + UUID.randomUUID());
    	 req1.setRegName("김수정");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/modify")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    /**
     * 게시판 수정 (없는 글)
     * 
     * @throws Exception
     */
    @Test
    public void modifyBoardNoResult() throws Exception {
    	 final BoardModifyReqDto req1 = new BoardModifyReqDto();
    	 req1.setSeq("9999999999");
    	 req1.setSubject("제목(수정) - " + UUID.randomUUID());
    	 req1.setContent("내용(수정) -" + UUID.randomUUID());
    	 req1.setRegName("김수정");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/modify")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.errors").isArray())
		                    .andExpect(jsonPath("$.errors[0].bizErrorCode").value("modify.E001"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    /**
     * 게시판 삭제 (없는 글)
     * 
     * @throws Exception
     */
    @Test
    public void deleteBoardNoResult() throws Exception {
    	 final BoardDeleteReqDto req1 = new BoardDeleteReqDto();
    	 req1.setSeq("9999999999");
    	
    	//given
    	String param = objectMapper.writeValueAsString(req1);
    	
    	//when
    	MvcResult result = this.mockMvc.perform(post("/v1/board/modify")
		        			.contentType(MediaType.APPLICATION_JSON).content(param))
		                    .andExpect(status().isOk())
		                    .andExpect(jsonPath("$.httpStatus").value("200"))
		                    .andExpect(jsonPath("$.errors").isArray())
		                    .andExpect(jsonPath("$.errors[0].bizErrorCode").value("delete.E001"))
		                    .andDo(print())
		        			.andReturn();
        
    	//then
        String stringResult = result.getResponse().getContentAsString();
        logger.info(stringResult);
    }
    
    
}
