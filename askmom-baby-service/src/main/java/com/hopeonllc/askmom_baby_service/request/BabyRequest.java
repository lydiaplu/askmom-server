package com.hopeonllc.askmom_baby_service.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BabyRequest {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    @Size(max = 100, message = "First name length cannot exceed 100")
    private String firstName;

    @Size(max = 100, message = "Last name length cannot exceed 100")
    private String lastName;

    @Size(max = 100, message = "Middle name length cannot exceed 100")
    private String middleName;

    @Size(max = 100, message = "Nickname length cannot exceed 100")
    private String nickname;

    @Size(max = 20, message = "Gender length cannot exceed 20")
    private String gender;

    private LocalDate birthDate;

    @Pattern(
            regexp = "^(breastfeeding|formula|mixed|unknown)$",
            message = "Feeding type must be breastfeeding, formula, mixed or unknown"
    )
    private String feedingType;
}
