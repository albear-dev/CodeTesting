package analyzer.app.common.exception;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import analyzer.app.common.messagesource.MessageSourceProvider;


public class ExpectedException extends ResponseStatusException {
	private static final long serialVersionUID = 1L;

	public final static String BIZ_ETC_ERROR_CODE = "common.E001";
	
	@NotNull
	private String code;
	private Object[] args;
	private HttpStatus status;
	
	public ExpectedException(String code) {
		this(code, null, HttpStatus.OK, null);
    }
	
    public ExpectedException(String code, Object[] args) {
    	this(code, args, HttpStatus.OK, null);
    }

    public ExpectedException(String code, Object[] args, HttpStatus status) {
    	this(code, args, status, null);
    }
    
    public ExpectedException(String code, Object[] args, HttpStatus status, Throwable cause) {
        super(status, MessageSourceProvider.getMessage(code), cause);
        this.code = code;
        this.args = args;
        this.status = status;
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

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}


	
}
