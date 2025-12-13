package com.springboot.medease.Security;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class CustomUserPrincipal {
    private final String userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserPrincipal(String userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}
