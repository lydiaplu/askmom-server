package com.hopeonllc.askmom_user_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileRequest {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 100, message = "First name length cannot exceed 100")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 100, message = "Last name length cannot exceed 100")
    private String lastName;

    @Size(max = 100, message = "Middle name length cannot exceed 100")
    private String middleName;

    @Size(max = 20, message = "Gender length cannot exceed 20")
    private String gender;

    private LocalDate birthDate;

    @Size(max = 30, message = "Phone number length cannot exceed 30")
    private String phoneNumber;

    @NotBlank(message = "Preferred language cannot be empty")
    @Size(max = 10, message = "Preferred language length cannot exceed 10")
    private String preferredLanguage;

    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    private String countryCode;

    @Size(max = 100, message = "Country name length cannot exceed 100")
    private String countryName;

    @Size(max = 100, message = "State/Province length cannot exceed 100")
    private String stateProvince;

    @Size(max = 100, message = "City length cannot exceed 100")
    private String city;

    @Size(max = 100, message = "District length cannot exceed 100")
    private String district;

    @Size(max = 20, message = "Postal code length cannot exceed 20")
    private String postalCode;

    @Size(max = 255, message = "Address line 1 length cannot exceed 255")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 length cannot exceed 255")
    private String addressLine2;

    @Size(max = 500, message = "Full address length cannot exceed 500")
    private String fullAddress;

    @Size(max = 50, message = "Timezone length cannot exceed 50")
    private String timezone;
}
