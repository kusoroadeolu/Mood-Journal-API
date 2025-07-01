package com.victor.moodjournal.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
@Getter
public enum Role implements GrantedAuthority {
    ADMIN("ADMIN"),
    USER("USER");

    private final String authority;

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
