package com.hopeonllc.askmom_user_service.mapper;

import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.request.UserRequest;
import com.hopeonllc.askmom_user_service.response.UserResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User toModel(UserRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponses(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(User source, @MappingTarget User target);
}
