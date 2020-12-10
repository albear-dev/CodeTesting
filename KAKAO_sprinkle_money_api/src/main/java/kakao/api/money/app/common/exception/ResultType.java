package kakao.api.money.app.common.exception;

public enum ResultType {
	// main result type
	SUCCESS 			("200", "성공"),
	
	INVALID_INPUT_VALUE	("400", "잘못된 요청입니다"),
    METHOD_NOT_ALLOWED	("405", "허용되지 않는 서비스입니다."),
    
	BIZ_ERROR			("200", "업무 처리중 오류가 발생하였습니다."),
	SYS_ERROR			("500", "시스템 작업중 오류가 발생하였습니다")
    ;
	
	private final String status;
	private final String message;
    
	public String getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}

	ResultType(final String status, String message) {
        this.status 	= status;
        this.message	= message;
    }
}
