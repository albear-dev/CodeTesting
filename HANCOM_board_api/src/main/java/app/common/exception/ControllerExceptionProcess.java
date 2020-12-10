package app.common.exception;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.boardmanager.dto.CommonResDto;

public class ControllerExceptionProcess {
	private final static Logger logger = LoggerFactory.getLogger(ControllerExceptionProcess.class.getClass());
	
	public static ResponseEntity<CommonResDto> createExceptionResponseMessage(Exception e, MessageSource messageSource, Locale locale) throws Exception {
		if(e instanceof BizException) {
			return new ResponseEntity<>(
					CommonResDto.of(
						ResultType.BIZ_ERROR,
						((BizException)e).getCode(),
						messageSource.getMessage(((BizException)e).getCode(), null, locale)
					),
					HttpStatus.OK
				);
		}else{
			e.printStackTrace();
			if(logger.isErrorEnabled()) { logger.error("Error Message [{}]", e.getMessage()); }
			
			return new ResponseEntity<>(
				CommonResDto.of(
					ResultType.BIZ_ERROR,
					BizException.BIZ_ETC_ERROR_CODE,
					messageSource.getMessage(BizException.BIZ_ETC_ERROR_CODE, null, locale)
				),
				HttpStatus.OK
			);
		}
	}
}
