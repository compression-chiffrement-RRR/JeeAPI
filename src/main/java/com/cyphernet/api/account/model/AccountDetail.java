package com.cyphernet.api.account.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AccountDetail implements UserDetails {

    private final Account account;

    public AccountDetail(Account account) {
        super();
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO: implement later
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: implement later
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: implement later
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO: implement later
        return true;
    }
}
