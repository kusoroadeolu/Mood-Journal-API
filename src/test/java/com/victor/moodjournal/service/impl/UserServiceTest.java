package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.dto.LoginRequestDto;
import com.victor.moodjournal.dto.RegisterRequestDto;
import com.victor.moodjournal.dto.UserResponseDto;
import com.victor.moodjournal.exception.EmailAlreadyExistsException;
import com.victor.moodjournal.mapper.UserMapper;
import com.victor.moodjournal.model.Role;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserPrincipal;
import com.victor.moodjournal.repository.UserRepository;
import com.victor.moodjournal.service.JwtService;
import com.victor.moodjournal.service.MyUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private MyUserDetailsService myUserDetailsService;

    @InjectMocks
    private UserService userService;

    User user;
    UserDetails userDetails;

    @BeforeEach
    void setUp(){
        user =  User.builder()
                .email("smth@email")
                .username("test_name")
                .password("password")
                .createdAt(LocalDateTime.now())
                .role(Role.USER)
                .build();
        userDetails = new UserPrincipal(user);

    }


    @Test
    public void should_ensure_a_user_is_authenticated(){
        LoginRequestDto request =
                new LoginRequestDto("test_name", "password");

        //when
        when(myUserDetailsService.loadUserByUsername(userDetails.getUsername()))
                .thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("test_token");

        String authToken = userService.authenticateUser(request);

        //then
        assertNotNull(authToken);
        verify(jwtService, atLeastOnce()).generateToken(userDetails);

    }

    @Test
    public void should_successfully_register_a_user()
            throws SQLIntegrityConstraintViolationException {
        //given
        UserResponseDto responseDto = new UserResponseDto(
          user.getUsername()
        );

        RegisterRequestDto registerRequest =
                new RegisterRequestDto(
                        "test_name",
                        "test_email",
                        "test_password"
                );

        //when
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(responseDto);

        //then
        UserResponseDto userResponseDto =
                userService.registerUser(registerRequest);

        verify(passwordEncoder, atLeastOnce()).encode(registerRequest.password());
        assertNotNull(userResponseDto);
        assertEquals(registerRequest.username(), userResponseDto.username());

    }


    @Test
    public void should_throw_email_exception_if_email_exists(){
        //given
        RegisterRequestDto registerRequest =
                new RegisterRequestDto(
                      "test_name",
                      "test_email",
                      "test_password"
                );

        //when
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        var message = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("This email has already been taken", message.getMessage());
    }

}