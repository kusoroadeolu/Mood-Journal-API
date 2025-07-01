package com.victor.moodjournal.service;

import com.victor.moodjournal.dto.LoginRequestDto;
import com.victor.moodjournal.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;

@Service
public interface JwtService {

    SecretKey generateKey(String secret);
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
    Claims extractAllClaims(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
