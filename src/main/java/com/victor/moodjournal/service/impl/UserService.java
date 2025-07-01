package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.dto.LoginRequestDto;
import com.victor.moodjournal.dto.RegisterRequestDto;
import com.victor.moodjournal.dto.UserResponseDto;
import com.victor.moodjournal.exception.EmailAlreadyExistsException;
import com.victor.moodjournal.mapper.UserMapper;
import com.victor.moodjournal.model.Role;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.repository.UserRepository;
import com.victor.moodjournal.service.JwtService;
import com.victor.moodjournal.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    public String authenticateUser(LoginRequestDto requestDto){

        UserDetails userDetails = myUserDetailsService
                .loadUserByUsername(requestDto.login());

        return jwtService.generateToken(userDetails);
    }

    public UserResponseDto registerUser(RegisterRequestDto userDto)
                            throws  SQLIntegrityConstraintViolationException{
        if(userRepository.existsByEmail(userDto.email())){
            throw new EmailAlreadyExistsException("This email has already been taken");
        }
        User user = User.builder()
                            .email(userDto.email())
                            .username(userDto.username())
                            .password(passwordEncoder.encode(userDto.password()))
                            .createdAt(LocalDateTime.now())
                            .role(Role.USER)
                            .build();
        var saved = userRepository.save(user);
        return userMapper.toUserResponseDto(saved);
    }


}
