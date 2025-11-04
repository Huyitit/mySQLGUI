package com.dbinteract.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.dbinteract.models.User;

public class UserPrincipal implements UserDetails {
    
    private final User user;
    
    public UserPrincipal(User user) {
        this.user = user;
    }
    
    public int getUserId() {
        return user.getUserId();
    }
    
    @Override
    public String getUsername() {
        return user.getUserName();
    }
    
    @Override
    public String getPassword() {
        return user.getHashedPassword();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
