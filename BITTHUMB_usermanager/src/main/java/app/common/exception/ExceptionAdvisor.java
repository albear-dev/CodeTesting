package app.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.usermanager.dto.CommonResDto;

@ControllerAdvice
@RestController
public class ExceptionAdvisor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	 
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	protected ResponseEntity<ExceptionErrorEntity> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//		logger.error("handleMethodArgumentNotValidException", e);
//		
//		final ExceptionErrorEntity response = ExceptionErrorEntity.of(ExceptionErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
//		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//		
//	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected CommonResDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		return CommonResDto.of(ResultType.INVALID_INPUT_VALUE, e.getBindingResult());
	}
	
}