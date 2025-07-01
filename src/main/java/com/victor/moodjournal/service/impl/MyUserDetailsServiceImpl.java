package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.exception.UserNotFoundException;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserPrincipal;
import com.victor.moodjournal.repository.UserRepository;
import com.victor.moodjournal.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsServiceImpl implements MyUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                                        .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
        return new UserPrincipal(user);
    }

    @Override
    public User loadDomainUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with username: " + username));
    }
}
