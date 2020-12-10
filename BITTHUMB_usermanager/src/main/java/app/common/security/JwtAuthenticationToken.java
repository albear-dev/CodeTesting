package app.common.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String token;
    
    public JwtAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String token) {
        super(principal, credentials, authorities);
        this.token = token;
    }
    
    public JwtAuthenticationToken(String token) {
    	super(null, null);
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
}

