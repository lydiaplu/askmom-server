package com.hopeonllc.askmom_baby_service.mapper;

import com.hopeonllc.askmom_baby_service.model.Baby;
import com.hopeonllc.askmom_baby_service.request.BabyRequest;
import com.hopeonllc.askmom_baby_service.response.BabyResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BabyMapper {

    Baby toModel(BabyRequest request);

    BabyResponse toResponse(Baby baby);

    List<BabyResponse> toResponses(List<Baby> babies);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateModel(Baby source, @MappingTarget Baby target);

    default String map(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    default String map(LocalDate value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    default String map(Baby.FeedingType value) {
        return value == null ? null : value.name();
    }

    default Baby.FeedingType map(String value) {
        return value == null ? null : Baby.FeedingType.valueOf(value);
    }
}
