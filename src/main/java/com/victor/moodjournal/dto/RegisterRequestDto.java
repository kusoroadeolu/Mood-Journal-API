package com.victor.moodjournal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Username is required")
        String username,

        @Email(message = "Please use a valid email")
        @NotBlank(message = "Email is required")
        String email ,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be atleast 6 characters")
        String password
) {
}
