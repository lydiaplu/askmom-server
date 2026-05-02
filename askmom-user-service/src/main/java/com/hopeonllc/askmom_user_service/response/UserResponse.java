package com.hopeonllc.askmom_user_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {

    private String id;

    private String email;

    private String createdAt;

    private String updatedAt;

    private String active;

    private String lastLoginAt;

    private String profileCompleted;

    private String emailVerified;

    private String emailVerifiedAt;

    private String authProvider;

    private String providerUserId;
}
