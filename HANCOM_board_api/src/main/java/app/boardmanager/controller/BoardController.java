package app.boardmanager.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.boardmanager.dao.BoardDao;
import app.boardmanager.dto.BoardDeleteReqDto;
import app.boardmanager.dto.BoardDetailReqDto;
import app.boardmanager.dto.BoardDetailResDto;
import app.boardmanager.dto.BoardListReqDto;
import app.boardmanager.dto.BoardListResDto;
import app.boardmanager.dto.BoardModifyReqDto;
import app.boardmanager.dto.BoardRegReqDto;
import app.boardmanager.dto.CommonResDto;
import app.common.exception.BizException;
import app.common.exception.ControllerExceptionProcess;
import app.common.exception.ResultType;

@EnableAutoConfiguration
@RestController
public class BoardController {
	private int COUNT_PER_PAGE = 10;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MessageSource messageSource;
	@Autowired
	BoardDao boardListDao;
	
	@PostMapping(path = "/v1/board/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> list(@Validated @RequestBody BoardListReqDto reqDto, Locale locale) throws Exception {
		try {
			
			reqDto.setOffset((reqDto.getPage()-1)*COUNT_PER_PAGE);
			reqDto.setPageLen(COUNT_PER_PAGE);
			List<BoardListResDto> list = boardListDao.selectBoardList(reqDto);
			if(list == null) {
				throw new BizException("info.E001");
			}
			
			return new ResponseEntity<>(
				CommonResDto.of(ResultType.SUCCESS).setData(list),
				HttpStatus.OK
			);
			
		}catch(Exception e) {
			return ControllerExceptionProcess.createExceptionResponseMessage(e, messageSource, locale);
		}
	}
	
	
	/**
	 * 게시판 상세보기
	 * 
	 * @param reqDto
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/v1/board/detail", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> detail(@Validated @RequestBody BoardDetailReqDto reqDto, Locale locale) throws Exception {
		try {
			
			BoardDetailResDto resDto = boardListDao.selectBoardDetail(reqDto);
			if(resDto == null) {
				throw new BizException("detail.E001");
			}
			
			return new ResponseEntity<>(
				CommonResDto.of(ResultType.SUCCESS).setData(resDto),
				HttpStatus.OK
			);
			
		}catch(Exception e) {
			return ControllerExceptionProcess.createExceptionResponseMessage(e, messageSource, locale);
		}
	}
	
	/**
	 * 게시판 신규등록
	 * 
	 * @param reqDto
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/v1/board/insert", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> insert(@Validated @RequestBody BoardRegReqDto reqDto, Locale locale) throws Exception {
		try {
			int result = boardListDao.insertBoardContent(reqDto);
			if(result < 1) {
				throw new BizException("insert.E001");
			}
			
			return new ResponseEntity<>(CommonResDto.of(ResultType.SUCCESS), HttpStatus.OK);
			
		}catch(Exception e) {
			return ControllerExceptionProcess.createExceptionResponseMessage(e, messageSource, locale);
		}
	}
	
	/**
	 * 게시판 수정
	 * 
	 * @param reqDto
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/v1/board/modify", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> modify(@Validated @RequestBody BoardModifyReqDto reqDto, Locale locale) throws Exception {
		try {
			int result = boardListDao.modifyBoardContent(reqDto);
			if(result < 1) {
				throw new BizException("modify.E001");
			}
			
			return new ResponseEntity<>(CommonResDto.of(ResultType.SUCCESS), HttpStatus.OK);
			
		}catch(Exception e) {
			return ControllerExceptionProcess.createExceptionResponseMessage(e, messageSource, locale);
		}
	}
	
	/**
	 * 게시판 삭제
	 * 
	 * @param reqDto
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "/v1/board/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResDto> delete(@Validated @RequestBody BoardDeleteReqDto reqDto, Locale locale) throws Exception {
		try {
			int result = boardListDao.deleteBoardContent(reqDto);
			if(result < 1) {
				throw new BizException("delete.E001");
			}
			
			return new ResponseEntity<>(CommonResDto.of(ResultType.SUCCESS), HttpStatus.OK);
			
		}catch(Exception e) {
			return ControllerExceptionProcess.createExceptionResponseMessage(e, messageSource, locale);
		}
	}
	
	
}
