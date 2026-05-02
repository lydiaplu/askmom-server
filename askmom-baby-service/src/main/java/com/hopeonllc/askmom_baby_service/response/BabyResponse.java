package com.hopeonllc.askmom_baby_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BabyResponse {

    private String id;

    private String userId;

    private String firstName;

    private String lastName;

    private String middleName;

    private String nickname;

    private String gender;

    private String birthDate;

    private String feedingType;

    private String createdAt;

    private String updatedAt;
}
