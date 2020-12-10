package app.common.security;

import java.util.Collection;
import java.util.Collections;

import javax.security.auth.Subject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticatedUser implements Authentication {
    private String email;
    private String username;
    private String[] roles;

    public AuthenticatedUser(String email, String username, String[] roles) {
        this.email = email;
        this.username = username;
        this.roles    = roles;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }

    @Override
    public boolean isAuthenticated() {
        return !StringUtils.isEmpty(email) && !StringUtils.isEmpty(username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }
}
