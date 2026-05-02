package com.hopeonllc.askmom_user_service.mapper;

import com.hopeonllc.askmom_user_service.model.UserProfile;
import com.hopeonllc.askmom_user_service.request.UserProfileRequest;
import com.hopeonllc.askmom_user_service.response.UserProfileResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "user.id", source = "userId")
    UserProfile toModel(UserProfileRequest request);

    @Mapping(target = "userId", source = "user.id")
    UserProfileResponse toResponse(UserProfile userProfile);

    List<UserProfileResponse> toResponses(List<UserProfile> userProfiles);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateModel(UserProfile source, @MappingTarget UserProfile target);

    default String map(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    default String map(LocalDate value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
