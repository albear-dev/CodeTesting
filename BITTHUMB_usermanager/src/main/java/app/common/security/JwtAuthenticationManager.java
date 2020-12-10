package app.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationManager implements AuthenticationManager {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	if(logger.isDebugEnabled()) { logger.debug(">>>>>  authentication object identityHashCode [{}]", System.identityHashCode(authentication)); }
    	
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        if(logger.isDebugEnabled()) { logger.debug(">>>>> jwtAuthenticationToken value [{}]", jwtAuthenticationToken); }
        String token = (String) jwtAuthenticationToken.getToken();

        AuthenticatedUser user = jwtProvider.parseToken(token);
        
        if (user == null) {
            throw new JwtTokenMalformedException("JWT token is not valid");
        } else if (!user.isAuthenticated()) {
            throw new JwtTokenMalformedException("required field is wrong.");
        }

        return user;
    }
}