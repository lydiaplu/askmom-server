package com.hopeonllc.askmom_maternal_service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaternalProfileResponse {

    private String id;

    private String userId;

    private String dueDate;

    private String deliveryDate;

    private String babyCount;

    private String maternalStage;

    private String deliveryType;

    private String createdAt;

    private String updatedAt;
}
