package app.boardmanager.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import app.common.exception.ResultType;
import app.common.messagesource.MessageSourceProvider;

@JsonInclude(Include.NON_NULL)
public class CommonResDto implements Serializable{
		private int httpStatus;
		private String resultMessage;
		private List<CommonResDetailDto> errors;
		private Object data;
		
		public int getHttpStatus() {
			return httpStatus;
		}
		public void setHttpStatus(int httpStatus) {
			this.httpStatus = httpStatus;
		}
		public String getResultMessage() {
			return resultMessage;
		}
		public void setResultMessage(String resultMessage) {
			this.resultMessage = resultMessage;
		}
		public List<CommonResDetailDto> getErrors() {
			return errors;
		}
		public Object getData() {
			return data;
		}
		public CommonResDto() {}
		public CommonResDto(int httpStatus, String resultMessage) {
			this(httpStatus, resultMessage, null);
		}
		
		public CommonResDto(int httpStatus, String resultMessage, List<CommonResDetailDto> errors) {
			this.httpStatus 	= httpStatus;
			this.resultMessage 	= resultMessage;
			this.errors			= errors;
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
			return new CommonResDto(resultType.getStatus(),resultType.getMessage());
		}
		
		public static CommonResDto of(ResultType resultType, String bizErrorCode, String bizErrorMsg) {
			return new CommonResDto(resultType.getStatus(), resultType.getMessage(), CommonResDetailDto.of(bizErrorCode, bizErrorMsg));
		}
		
		public static CommonResDto of(ResultType errorCode, BindingResult bindingResult) {
	    	List<CommonResDetailDto> errors =
	    			bindingResult.getFieldErrors()
	                   .stream()
	                   .map(error -> CommonResDetailDto.of(error))
	                   .collect(Collectors.toList());
	    	
	    	return new CommonResDto(errorCode.getStatus(), errorCode.getMessage(), errors);
	    }
		
		@JsonInclude(Include.NON_NULL)
		public static class CommonResDetailDto {
			private String objectName;
	        private String field;
			private String bizErrorCode;
			private String bizErrorMsg;
			
			public CommonResDetailDto(String objectName, String field, String bizErrorCode, String bizErrorMsg){
				this.objectName 	= objectName;
				this.field 			= field;
				this.bizErrorCode 	= bizErrorCode;
				this.bizErrorMsg	= bizErrorMsg;
			}
			
			public CommonResDetailDto(String bizErrorCode, String bizErrorMsg){
				this.objectName 	= null;
				this.field 			= null;
				this.bizErrorCode 	= bizErrorCode;
				this.bizErrorMsg	= bizErrorMsg;
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

			public String getBizErrorCode() {
				return bizErrorCode;
			}

			public void setBizErrorCode(String bizErrorCode) {
				this.bizErrorCode = bizErrorCode;
			}

			public String getBizErrorMsg() {
				return bizErrorMsg;
			}

			public void setBizErrorMsg(String bizErrorMsg) {
				this.bizErrorMsg = bizErrorMsg;
			}

			public static List<CommonResDetailDto> of(String bizErrorCode, String bizErrorMsg) {
				return Arrays.asList(new CommonResDetailDto(bizErrorCode, bizErrorMsg));
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
