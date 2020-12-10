package app.common.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import app.usermanager.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author hyeonny.kim
 * 
 * JWT 토큰 처리 지원 유틸 (파싱/검증/생성)
 */
@Component
public class JwtProvider {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String JWT_AUTHER = "Bearer";

	@Value("${jwt.key}")
	private String SECRET_KEY;
	@Value("${jwt.expirationTime}")
	private int EXPIRATION_TIME;
	
	@Autowired
	MemberRepository memberRepository;

	// TODO 활성화 할것
//	@PostConstruct
//	protected void init() {
//		SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
//	}

	/**
	 * @param userId
	 * @param userName
	 * @param roles
	 * @return String
	 * 
	 * JWT 토큰 생성
	 */
	public String createToken(String userId, String userName, String[] roles) {
		Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
		
		Claims claims = Jwts.claims().setId(userId);
		claims.put("roles", roles);
		claims.setExpiration(expirationDate);
        claims.put("userName", userName);

		if(logger.isInfoEnabled()) {
			logger.info("토큰 생성 - 만료일   [{}]", expirationDate);
			logger.info("토큰 생성 - ROLES [{}]", roles);
			logger.info("토큰 생성 - 유저명   [{}]", userName);
		}
		
		return Jwts.builder()
				.setClaims(claims) 								// 데이터
				.setIssuedAt(new Date()) 						// 토큰 발행일자
				.setExpiration(expirationDate) 					// set Expire Time
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 암호화 알고리즘, secret값 세팅
				.compact();
	}
	
	/**
	 * @param token
	 * @return boolean
	 * 
	 * 토큰검증
	 */
	public boolean verifyToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @param token
	 * @return io.jsonwebtoken.Claims
	 * 
	 * 토큰에서 ClaimBody 추출
	 */
	public Claims getClaimsBody(String token) {
		if(logger.isDebugEnabled()) {
			logger.debug(">>>>> SECRET_KEY [{}]", SECRET_KEY);
			logger.debug(">>>>> TOKEN      [{}]",token);
		}
		
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
	}
	
	/**
	 * @param roles
	 * @return List<GrantedAuthority>
	 * 
	 * String Array 에 있는 roles 정보를 GrantedAuthority 가 담긴 List 로 전환
	 */
	public List<GrantedAuthority> getAuthorities(String[] roles) {
      return roles.length == 0 || roles == null ?
              Collections.emptyList() :
              Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	/**
	 * @param token
	 * @return AuthenticatedUser
	 * 
	 * JWT Token 을 파싱하여 claim body의 중요 내용을 AuthenticatedUser에 담아 리턴
	 */
	public AuthenticatedUser parseToken(String token) {
		try {
			if(logger.isDebugEnabled()) {
				logger.debug(">>>>> SECRET_KEY [{}]", SECRET_KEY);
				logger.debug(">>>>> TOKEN      [{}]",token);
			}
			
			Claims claimsBody = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    		logger.info("claimsBody: {}", claimsBody);
			ArrayList<String> roles 	= (ArrayList<String>)claimsBody.get("roles");
			AuthenticatedUser user = new AuthenticatedUser(claimsBody.getId(), (String) claimsBody.get("userName"), roles.toArray(new String[roles.size()]));
			user.setAuthenticated(true);
			
			return user;
		} catch (ExpiredJwtException e) {
			logger.error(e.getMessage());
		} catch (JwtException | ClassCastException e) {
			logger.error("jwt parse error.", e);
		}

		return null;
	}
	


}