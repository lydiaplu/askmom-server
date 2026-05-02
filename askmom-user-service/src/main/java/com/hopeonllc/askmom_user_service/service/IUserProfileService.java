package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.model.UserProfile;

import java.util.List;

public interface IUserProfileService {

    UserProfile addNewUserProfile(UserProfile requestUserProfile);

    UserProfile updateUserProfile(Long id, UserProfile requestUserProfile);

    List<UserProfile> getAllUserProfiles();

    UserProfile getUserProfileById(Long id);

    UserProfile getUserProfileByUserId(Long userId);

    void deleteUserProfile(Long id);
}
