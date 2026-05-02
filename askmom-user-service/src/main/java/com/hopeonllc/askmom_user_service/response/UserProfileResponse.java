package com.hopeonllc.askmom_user_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponse {

    private String id;

    private String userId;

    private String firstName;

    private String lastName;

    private String middleName;

    private String gender;

    private String birthDate;

    private String phoneNumber;

    private String preferredLanguage;

    private String countryCode;

    private String countryName;

    private String stateProvince;

    private String city;

    private String district;

    private String postalCode;

    private String addressLine1;

    private String addressLine2;

    private String fullAddress;

    private String timezone;

    private String createdAt;

    private String updatedAt;
}
