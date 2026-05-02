package com.hopeonllc.askmom_user_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User cannot be null")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_user_profile_user")
    )
    private User user;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 100, message = "First name length cannot exceed 100")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(max = 100, message = "Last name length cannot exceed 100")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Size(max = 100, message = "Middle name length cannot exceed 100")
    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Size(max = 20, message = "Gender length cannot exceed 20")
    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 30, message = "Phone number length cannot exceed 30")
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @NotBlank(message = "Preferred language cannot be empty")
    @Size(max = 10, message = "Preferred language length cannot exceed 10")
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage = "EN";

    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    @Column(name = "country_code", columnDefinition = "CHAR(2)")
    private String countryCode;

    @Size(max = 100, message = "Country name length cannot exceed 100")
    @Column(name = "country_name", length = 100)
    private String countryName;

    @Size(max = 100, message = "State/Province length cannot exceed 100")
    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Size(max = 100, message = "City length cannot exceed 100")
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100, message = "District length cannot exceed 100")
    @Column(name = "district", length = 100)
    private String district;

    @Size(max = 20, message = "Postal code length cannot exceed 20")
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Size(max = 255, message = "Address line 1 length cannot exceed 255")
    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 length cannot exceed 255")
    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Size(max = 500, message = "Full address length cannot exceed 500")
    @Column(name = "full_address", length = 500)
    private String fullAddress;

    @Size(max = 50, message = "Timezone length cannot exceed 50")
    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
