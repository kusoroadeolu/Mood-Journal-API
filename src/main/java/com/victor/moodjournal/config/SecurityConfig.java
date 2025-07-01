package com.victor.moodjournal.config;

import com.victor.moodjournal.filter.JwtFilter;
import com.victor.moodjournal.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final JwtFilter jwtFilter; // Keep injecting your JwtFilter
    private final MyUserDetailsService userDetailsService;

    // Define the public paths as constants for clarity and consistency
    private static final String[] PUBLIC_URLS = {
            "/api/v1/users/register",
            "/api/v1/users/login",
            "/ping"
    };



    @Bean
    public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(PUBLIC_URLS)
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                .build();
    }


    // 2. Second SecurityFilterChain for All Other (Protected) Endpoints
    // This chain will handle all requests that were NOT matched by publicFilterChain.
    @Bean
    public SecurityFilterChain privateFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API
                .authorizeHttpRequests(
                        requests -> requests.anyRequest().authenticated() // All remaining requests must be authenticated
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}