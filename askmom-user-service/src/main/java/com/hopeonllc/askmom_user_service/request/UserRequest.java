package com.hopeonllc.askmom_user_service.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email length cannot exceed 255")
    private String email;

    @NotBlank(message = "Password hash cannot be empty")
    @Size(max = 255, message = "Password hash length cannot exceed 255")
    private String passwordHash;

    private Boolean active;

    private LocalDateTime lastLoginAt;

    private Boolean profileCompleted;

    private Boolean emailVerified;

    private LocalDateTime emailVerifiedAt;

    @Pattern(regexp = "^(email|google|apple)?$", message = "Auth provider must be email, google or apple")
    @Size(max = 50, message = "Auth provider length cannot exceed 50")
    private String authProvider;

    @Size(max = 255, message = "Provider user id length cannot exceed 255")
    private String providerUserId;
}
