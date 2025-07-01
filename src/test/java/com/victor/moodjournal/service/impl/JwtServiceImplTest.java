package com.victor.moodjournal.service.impl;

import com.victor.moodjournal.config.JwtProperties;
import com.victor.moodjournal.model.Role;
import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.websocket.DecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private JwtProperties properties;

    @Spy
    @InjectMocks
    private JwtServiceImpl jwtService;

    private final String secret =
            "thisisalongsecretkeythatisatleast256bitslongforhs256";
    private final Long expiration =
            86400L;

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
    public void should_generate_a_jwt_token(){
        //given
        when(properties.getExpiration()).thenReturn(86400L);
        when(properties.getSecret()).thenReturn(secret);

        //when
        String token =
                jwtService.generateToken(userDetails);


        //then
        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");

        //verify
        verify(properties, atLeastOnce()).getExpiration();
        verify(properties, atLeastOnce()).getSecret();

    }

    @Test
    public void should_extract_claims_from_valid_token(){
    //given
     when(properties.getSecret()).thenReturn(secret);
     when(properties.getExpiration()).thenReturn(3600000L);

     String token = jwtService.generateToken(userDetails);

     //when
      Claims claims = jwtService.extractAllClaims(token);

      assertNotNull(claims);
      assertEquals(userDetails.getUsername(), claims.getSubject());
      verify(properties, atLeastOnce()).getSecret();

    }

    @Test
    public void should_throw_expired_jwt_exception(){
        //given
        when(properties.getSecret()).thenReturn(secret);
        when(properties.getExpiration()).thenReturn(0L);

        //when
        String token = jwtService.generateToken(userDetails);

        //then
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractAllClaims(token);
        });
    }

    @Test
    public void should_throw_jwt_signature_exception(){
        //given
        when(properties.getSecret()).thenReturn(secret);
        when(properties.getExpiration()).thenReturn(3600L);

        String token = jwtService.generateToken(userDetails);

        //when
        String badSecret = "thisisnotalongsecretkeythatisatleast256bitslongforhs257";
        when(properties.getSecret()).thenReturn(badSecret);

        //then
        assertThrows(SignatureException.class, () -> {
           jwtService.extractAllClaims(token);
        });
    }

    @Test
    public void should_throw_malformed_jwt_exception(){
        //given
        when(properties.getSecret()).thenReturn(secret);

        String malformedToken = "abc.123.def";

        //then
        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractAllClaims(malformedToken);
        });
    }

    @Test
    public void should_extract_user_name(){
        //given
        when(properties.getSecret()).thenReturn(secret);
        when(properties.getExpiration()).thenReturn(expiration);
        String token = jwtService.generateToken(userDetails);

        //when
        String username = jwtService.extractUsername(token);

        //then
        verify(jwtService, atLeastOnce()).extractAllClaims(token);
        assertNotNull(username);
        assertEquals(userDetails.getUsername(), username);

    }

    @Test
    public void should_return_true_if_token_is_valid(){
        when(properties.getSecret()).thenReturn(secret);
        when(properties.getExpiration()).thenReturn(expiration);
        String token = jwtService.generateToken(userDetails);

        //when
        boolean isValid = jwtService.validateToken(token, userDetails);

        //then
        assertTrue(isValid);
    }


}
