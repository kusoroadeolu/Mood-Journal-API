package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.exception.UserNotFoundException;
import com.victor.moodjournal.model.Role;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    public void setUp(){
        user =  User.builder()
                .email("smth@email")
                .username("test_name")
                .password("password")
                .createdAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
    }

    @Test
    public void should_load_user_by_username(){
        //given
        String username = "test_name";

        //when
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);

        //then
        verify(userRepository, atLeastOnce()).findByUsername(username);
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals("ROLE_" + user.getRole().getAuthority(),
                userDetails.getAuthorities()
                        .stream()
                        .toList()
                        .getFirst()
                        .toString());
    }


    @Test
    public void should_throw_exception_if_user_not_found(){
        String username = "idontexist";

        //when
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //then
        var ex = assertThrows(UserNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        assertEquals("Could not find user with username: " + username, ex.getMessage());
    }
}