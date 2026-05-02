package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.exception.DataValidationException;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserProfileAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserProfileMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.model.UserProfile;
import com.hopeonllc.askmom_user_service.repository.UserProfileRepository;
import com.hopeonllc.askmom_user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService implements IUserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile addNewUserProfile(UserProfile requestUserProfile) {
        Long userId = extractUserId(requestUserProfile);
        validateUserProfileForCreate(userId);

        UserProfile userProfile = new UserProfile();
        userProfileMapper.updateModel(requestUserProfile, userProfile);
        userProfile.setUser(resolveUserById(userId));

        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        log.info("User profile created: id={}, userId={}", savedUserProfile.getId(), savedUserProfile.getUser().getId());
        return savedUserProfile;
    }

    @Override
    public UserProfile updateUserProfile(Long id, UserProfile requestUserProfile) {
        UserProfile existingUserProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));

        Long requestedUserId = extractUserId(requestUserProfile);
        validateUserProfileForUpdate(id, existingUserProfile, requestedUserId);

        userProfileMapper.updateModel(requestUserProfile, existingUserProfile);
        existingUserProfile.setUser(resolveUserById(requestedUserId));

        UserProfile updatedUserProfile = userProfileRepository.save(existingUserProfile);
        log.info("User profile updated: id={}, userId={}", updatedUserProfile.getId(), updatedUserProfile.getUser().getId());
        return updatedUserProfile;
    }

    @Override
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    @Override
    public UserProfile getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with id: " + id));
    }

    @Override
    public UserProfile getUserProfileByUserId(Long userId) {
        return userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found with user id: " + userId));
    }

    @Override
    public void deleteUserProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("User profile not found with id: " + id);
        }
        userProfileRepository.deleteById(id);
        log.info("User profile deleted: id={}", id);
    }

    private void validateUserProfileForCreate(Long userId) {
        if (userProfileRepository.existsByUser_Id(userId)) {
            log.warn("Create user profile validation failed: user profile already exists, userId={}", userId);
            throw new UserProfileAlreadyExistsException("User profile already exists for user id: " + userId);
        }
    }

    private void validateUserProfileForUpdate(Long profileId, UserProfile existingUserProfile, Long requestedUserId) {
        Long currentUserId = existingUserProfile.getUser().getId();
        if (!currentUserId.equals(requestedUserId) && userProfileRepository.existsByUser_Id(requestedUserId)) {
            log.warn("Update user profile validation failed: target user already has profile, profileId={}, userId={}",
                    profileId, requestedUserId);
            throw new UserProfileAlreadyExistsException("User profile already exists for user id: " + requestedUserId);
        }
    }

    private Long extractUserId(UserProfile userProfile) {
        if (userProfile == null || userProfile.getUser() == null || userProfile.getUser().getId() == null) {
            throw new DataValidationException("User id cannot be empty");
        }
        return userProfile.getUser().getId();
    }

    private User resolveUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user;
    }
}
