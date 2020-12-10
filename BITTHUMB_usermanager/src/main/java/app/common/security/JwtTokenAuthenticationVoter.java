package app.common.security;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author hyeonny.kim
 * 
 * 토큰검증시 기본 Voter가 roles에 대한 검증이 되지 않아 별도 구현
 */
public class JwtTokenAuthenticationVoter implements AccessDecisionVoter<Object> {

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class clazz) {
		return true;
	}

	@Override
	public int vote(
	  Authentication authentication, Object object, Collection collection) {
	    return authentication.getAuthorities().stream()
	      .map(GrantedAuthority::getAuthority)
	      .filter(r -> "ROLE_USER".equals(r))
	      .findAny()
	      .map(s -> ACCESS_DENIED)
	      .orElseGet(() -> ACCESS_ABSTAIN);
	}

}
