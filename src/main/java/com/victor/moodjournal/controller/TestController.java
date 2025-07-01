package com.victor.moodjournal.controller;

import com.victor.moodjournal.model.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public String me(@AuthenticationPrincipal UserPrincipal currentUser){
        return "Welcome to your me page " + currentUser.getUsername();
    }

    @GetMapping("/ping")
    public String ping(){
        return "Ping successful";
    }

}
