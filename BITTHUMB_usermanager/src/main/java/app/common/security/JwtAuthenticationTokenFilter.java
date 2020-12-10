package app.common.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;

public class JwtAuthenticationTokenFilter extends BasicAuthenticationFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String TOKEN_HEADER_KEY = "Authorization";

	private JwtProvider jwt;
	private AuthenticationManager authenticationManager;

	/**
	 * @param authenticationManager
	 * @param jwt
	 * 
	 * Spring Security 에서 처리하는 필터. SecurityConfig에 필터 사용에 대한 설정이 정의되어 있다.
	 * 
	 * [NOTE] JwtAuthenticationTokenFilter는 Component 가 아니므로 스프링에서 관리하지 않기 때문에 Autowired 되지 않음.
	 */
	public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager, JwtProvider jwt) {
		super(authenticationManager);
		this.authenticationManager = authenticationManager;
		this.jwt = jwt;
	}

	/**
	 * Filter Class 가 수행되면 실행되는 Method
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// Request에서 Token 값을 추출한다.
		String reqToken = request.getHeader(TOKEN_HEADER_KEY);
		String jwtToken = null;
		if (!StringUtils.isBlank(reqToken)) {
			jwtToken = reqToken.replace(JwtProvider.JWT_AUTHER, "").trim();
		}
		
		if(logger.isDebugEnabled()) { logger.debug(">>>>> Token Value [{}]", jwtToken); }

		if (jwtToken != null) {
			// 1. 토큰 유효성 검증 수행
			if (jwt.verifyToken(jwtToken)) {
				
				// 2. 토큰에서 Claim 정보 추출
				Claims claimsBody = jwt.getClaimsBody(jwtToken);
				if(logger.isDebugEnabled()) { logger.debug(">>>>> claimsBody [{}]", claimsBody); }
				
				// 3. Claim 정보에서 roles 정보 추출
				ArrayList<String> roles = (ArrayList<String>) claimsBody.get("roles");
				List<GrantedAuthority> authorities = jwt.getAuthorities(roles.toArray(new String[roles.size()]));

				String id = claimsBody.getId();
//				String userName = (String) claimsBody.get("userName");

				// 4. SpringSecurity 에서 사용하는 유저 정보 Entity에 토큰에서 추출한 유저 정보의 최소값을 세팅한다.
				UserDetails userDetails = new User(id, "", authorities);

				// 5.인증작업시 참조한 토큰정보의 구현체인 JwtAuthenticationToken 을 생성한다.
				JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userDetails, null,
						userDetails.getAuthorities(), jwtToken);
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// 6. authenticationManager를 통하여 인가 정보 처리
				Authentication authentication = authenticationManager.authenticate(authenticationToken);
				if(logger.isInfoEnabled()) { logger.info(">>>>> isAuthenticated? - USER[{id}] Authenticated[{}]",id ,authentication.isAuthenticated()); }
				
				// 7. 인가 정보가 담긴 Authentication객체를 Spring Security에서 사용하는 Context 에 저장
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		// Continue filter execution
		chain.doFilter(request, response);
	}

}