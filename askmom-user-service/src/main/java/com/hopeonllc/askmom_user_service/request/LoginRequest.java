package com.hopeonllc.askmom_user_service.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email length cannot exceed 255")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(max = 255, message = "Password length cannot exceed 255")
    private String password;
}
