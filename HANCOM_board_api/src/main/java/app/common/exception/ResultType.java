package app.common.exception;

public enum ResultType {
	// main result type
	SUCCESS 			(200, "성공"),
	
	INVALID_INPUT_VALUE	(400, "입력값 오류!"),
    METHOD_NOT_ALLOWED	(405, "허용되지 않는 서비스ID!"),
    
	BIZ_ERROR			(200, "업무 처리중 오류가 발생하였습니다.")
    ;
	
	private int status;
	private final String message;
    
	public int getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}

	ResultType(final int status, String message) {
        this.status 	= status;
        this.message	= message;
    }
}
