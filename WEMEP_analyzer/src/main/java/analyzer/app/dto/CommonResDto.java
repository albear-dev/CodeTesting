package analyzer.app.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import analyzer.app.common.exception.ResultType;
import analyzer.app.common.messagesource.MessageSourceProvider;

@JsonInclude(Include.NON_NULL)
public class CommonResDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	private List<CommonResDetailDto> errors;
	private Object data;
	
	public String getmessage() {
		return message;
	}
	public void setmessage(String message) {
		this.message = message;
	}
	public List<CommonResDetailDto> getErrors() {
		return errors;
	}
	public Object getData() {
		return data;
	}
	public String getCode() {
		return (code==null)?"200":code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public CommonResDto() {}
	public CommonResDto(String code, String message) {
		this(code, message, null);
	}
	
	public CommonResDto(String code, String message, List<CommonResDetailDto> errors) {
		this.code		= code;
		this.message 	= message;
		this.errors		= errors;
	}

	public CommonResDto setData(Object dataObject) {
		this.data = dataObject;
		return this;
	}
	
	public CommonResDto setErrors(List<CommonResDetailDto> dataObject) {
		this.errors = dataObject;
		return this;
	}
	
	public static CommonResDto of(ResultType resultType) {
		return new CommonResDto(resultType.getStatus(), resultType.getMessage());
	}
	
	public static CommonResDto of(ResultType resultType, String errorCode) {
		return new CommonResDto( resultType.getStatus()
								,resultType.getMessage()
								,CommonResDetailDto.of(errorCode, MessageSourceProvider.getMessage(errorCode))
								);
	}
	
	public static CommonResDto of(ResultType resultType, String errorCode, String errorMsg) {
		return new CommonResDto( resultType.getStatus()
								,resultType.getMessage()
								,CommonResDetailDto.of(errorCode, errorMsg));
	}
	
	public static CommonResDto of(ResultType errorCode, BindingResult bindingResult) {
    	List<CommonResDetailDto> errors =
    			bindingResult.getFieldErrors()
                   .stream()
                   .map(error -> CommonResDetailDto.of(error))
                   .collect(Collectors.toList());
    	
    	return new CommonResDto( errorCode.getStatus()
    							,errorCode.getMessage()
    							,errors);
    }
	
	@JsonInclude(Include.NON_NULL)
	public static class CommonResDetailDto {
		private String objectName;
        private String field;
		private String errorCode;
		private String errorMsg;
		
		public CommonResDetailDto(String objectName, String field, String errorCode, String errorMsg){
			this.objectName 	= objectName;
			this.field 			= field;
			this.errorCode 		= errorCode;
			this.errorMsg		= errorMsg;
		}
		
		public CommonResDetailDto(String errorCode, String errorMsg){
			this.objectName 	= null;
			this.field 			= null;
			this.errorCode 		= errorCode;
			this.errorMsg		= errorMsg;
		}
		
		public String getObjectName() {
			return objectName;
		}

		public void setObjectName(String objectName) {
			this.objectName = objectName;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}
		
		public String getErrorCode() {
			return errorCode;
		}

		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public static List<CommonResDetailDto> of(String errorCode, String errorMsg) {
			return Arrays.asList(new CommonResDetailDto(errorCode, errorMsg));
		}
		
		public static CommonResDetailDto of(FieldError fieldError) {
            return new CommonResDetailDto(
               fieldError.getObjectName(),
               fieldError.getField(),
               "common.E002",	// 입력값이 잘못되었습니다.
               "["+fieldError.getCode()+"] " + MessageSourceProvider.getMessage(fieldError.getDefaultMessage()));
        }
	}
	
}
