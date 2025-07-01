package com.victor.moodjournal.service;

import com.victor.moodjournal.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface MyUserDetailsService extends UserDetailsService {

    User loadDomainUserByUsername(String username);
}
