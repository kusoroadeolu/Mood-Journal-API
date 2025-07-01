package com.victor.moodjournal.service;

import com.victor.moodjournal.dto.AuthResponse;
import com.victor.moodjournal.dto.LoginRequestDto;
import com.victor.moodjournal.dto.RegisterRequestDto;
import com.victor.moodjournal.dto.UserDto;
import com.victor.moodjournal.model.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    User createUser(UserDto userDto);
    void registerUser(RegisterRequestDto registerRequest);
    AuthResponse authenticateUser(LoginRequestDto loginRequest);

}

