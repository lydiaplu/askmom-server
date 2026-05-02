package com.hopeonllc.askmom_maternal_service.mapper;

import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import com.hopeonllc.askmom_maternal_service.request.MaternalProfileRequest;
import com.hopeonllc.askmom_maternal_service.response.MaternalProfileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MaternalProfileMapper {

    MaternalProfile toModel(MaternalProfileRequest request);

    MaternalProfileResponse toResponse(MaternalProfile maternalProfile);

    List<MaternalProfileResponse> toResponses(List<MaternalProfile> maternalProfiles);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateModel(MaternalProfile source, @MappingTarget MaternalProfile target);

    default String map(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    default String map(Integer value) {
        return value == null ? null : String.valueOf(value);
    }

    default String map(LocalDate value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
