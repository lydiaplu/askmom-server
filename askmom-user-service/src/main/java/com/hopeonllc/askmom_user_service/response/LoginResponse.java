package com.hopeonllc.askmom_user_service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String tokenType;
    private String accessToken;
}
