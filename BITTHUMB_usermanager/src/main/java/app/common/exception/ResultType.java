package app.common.exception;

import lombok.Getter;

public enum ResultType {
	// main result type
	SUCCESS 			(200, "성공"),
	
	INVALID_INPUT_VALUE	(400, "입력값 오류!"),
    METHOD_NOT_ALLOWED	(405, "허용되지 않는 서비스ID!"),
    
	BIZ_ERROR			(200, "업무 처리중 오류가 발생하였습니다.")
    ;
	
	// 위 추가속성 순서대로 생성자에서 세팅 가능
	@Getter
	private int status;
	@Getter
	private final String message;
    
	ResultType(final int status, String message) {
        this.status 	= status;
        this.message	= message;
    }
}
