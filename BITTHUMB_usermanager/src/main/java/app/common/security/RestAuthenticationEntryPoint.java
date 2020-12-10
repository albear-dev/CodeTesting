package app.common.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		
//		List<String> errors = new ArrayList<>();
//        errors.add("Unauthorized");
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 1. errorController에  /error 정의하고 그 주소로 들어오면 throw Exception 시킨 후 ExceptionAdvisor에서 처리..
        //    이건 잘못된 포맷 등의 500 에러 처리 가능..
        // 2. 그냥 여기서 Json Object 로 리턴..
        //    500 에러는 별도 처리해야 함...
	}
}
