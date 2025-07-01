package com.victor.moodjournal.filter;

import com.victor.moodjournal.model.User;
import com.victor.moodjournal.model.UserPrincipal;
import com.victor.moodjournal.service.JwtService;
import com.victor.moodjournal.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {

    //private final MyUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ApplicationContext applicationContext;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);

            try{
            username = jwtService.extractUsername(token);
            }catch (Exception e){
                System.out.println("Malformed token: " + token);
            }

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

//                MyUserDetailsService userDetailsService =
//                                applicationContext.getBean(MyUserDetailsService.class);
//
//                User user = userDetailsService.loadDomainUserByUsername(username);
//                UserPrincipal principal = new UserPrincipal(user);

                UserPrincipal principal =
                        (UserPrincipal) applicationContext.getBean(MyUserDetailsService.class).loadUserByUsername(username);

                if(jwtService.validateToken(token, principal)){
                    UsernamePasswordAuthenticationToken authToken =
                                                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        }

        filterChain.doFilter(request, response);
    }
}
