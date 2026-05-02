package com.hopeonllc.askmom_maternal_service.request;

import jakarta.validation.constraints.Min;
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
public class MaternalProfileRequest {

    @NotNull(message = "User id cannot be null")
    private Long userId;

    private LocalDate dueDate;

    private LocalDate deliveryDate;

    @NotNull(message = "Baby count cannot be null")
    @Min(value = 1, message = "Baby count must be greater than or equal to 1")
    private Integer babyCount;

    @Pattern(
            regexp = "^(trying_to_conceive|pregnant|postpartum|parenting)$",
            message = "Maternal stage must be trying_to_conceive, pregnant, postpartum or parenting"
    )
    @Size(max = 30, message = "Maternal stage length cannot exceed 30")
    private String maternalStage;

    @Pattern(
            regexp = "^(vaginal|c_section|assisted_vaginal)$",
            message = "Delivery type must be vaginal, c_section or assisted_vaginal"
    )
    @Size(max = 50, message = "Delivery type length cannot exceed 50")
    private String deliveryType;
}
