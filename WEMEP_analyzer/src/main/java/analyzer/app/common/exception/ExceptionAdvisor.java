package analyzer.app.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import analyzer.app.dto.CommonResDto;

@RestControllerAdvice
public class ExceptionAdvisor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Spring MVC의 Validated 오류 Exception
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return CommonResDto.of(ResultType.INVALID_INPUT_VALUE, e.getBindingResult());
	}
	
	/**
	 * Webflux의 Validated 오류 Exception
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(WebExchangeBindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResDto handleWebExchangeBindException(WebExchangeBindException e) {
		return CommonResDto.of(ResultType.INVALID_INPUT_VALUE, e.getBindingResult());
	} 
	
	
	/**
	 * Header Validated를 오류 Exception
	 * TODO 해당 Exception은 응답 형식이 다른 필드 Validation 과는 달라 동일하게 맞출수 있을지 확인
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ServerWebInputException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResDto handleServerWebInputException(ServerWebInputException e) {
		return CommonResDto.of(ResultType.BIZ_ERROR, "400", e.getMessage());
	} 
	
	/**
	 * 비즈니스 처리중 오류
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ExpectedException.class)
	@ResponseStatus(HttpStatus.OK)
	protected CommonResDto handleExpectedException(ExpectedException e) {
		return CommonResDto.of(ResultType.BIZ_ERROR, e.getCode());
	}
	
	/**
	 * 기타 Exception 처리
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected CommonResDto handleException(Exception e) {
		return CommonResDto.of(ResultType.SYS_ERROR, "500", e.getMessage());
	}
}
