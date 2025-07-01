package com.victor.moodjournal.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserPrincipal implements UserDetails {

    public final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user != null ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getAuthority())) : null;
    }

    @Override
    public String getPassword() {
        return user != null  ? user.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    public Long getId(){
        return user != null ? user.getId() : null;
    }
}
