package com.victor.moodjournal.controller;

import com.victor.moodjournal.dto.*;
import com.victor.moodjournal.service.JwtService;
import com.victor.moodjournal.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;



    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto request)
                                                            throws SQLIntegrityConstraintViolationException {
        var response = userService.registerUser(request);
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequestDto request){
                var token = userService.authenticateUser(request);
                return ResponseEntity.ok().body(new JwtResponse(token));
    }


}
