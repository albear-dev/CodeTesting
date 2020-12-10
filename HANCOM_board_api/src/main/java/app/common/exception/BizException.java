package app.common.exception;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

public class BizException extends Exception {
	public final static String BIZ_ETC_ERROR_CODE = "common.E001";
	
	@NotNull
	private String code;
	private Object[] args;
	private Object status;
	
	public BizException(String code) {
		this(code, null);
	}
	
	public BizException(String code, Object[] args) {
		this(code, args, null);
	}
	
	public BizException(String code, Object status) {
		this(code, null, status);
	}
	
	public BizException(String code, Object[] args, Object status) {
		this.code 		= code;
		this.args 		= args;
		this.status 	= status==null?HttpStatus.OK:status;
	}

    public String getMessage(MessageSource messageSource, Locale locale) {
		return messageSource.getMessage(code, args, locale);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object getStatus() {
		return status;
	}

	public void setStatus(Object status) {
		this.status = status;
	}
    
    

	
}
