package com.hopeonllc.askmom_user_service.controller;

import com.hopeonllc.askmom_user_service.mapper.UserProfileMapper;
import com.hopeonllc.askmom_user_service.model.UserProfile;
import com.hopeonllc.askmom_user_service.request.UserProfileRequest;
import com.hopeonllc.askmom_user_service.response.UserProfileResponse;
import com.hopeonllc.askmom_user_service.service.IUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-profile")
public class UserProfileController {

    private final IUserProfileService userProfileService;
    private final UserProfileMapper userProfileMapper;

    @PostMapping("/add/new-user-profile")
    public ResponseEntity<UserProfileResponse> addNewUserProfile(@Valid @RequestBody UserProfileRequest request) {
        UserProfile savedUserProfile = userProfileService.addNewUserProfile(userProfileMapper.toModel(request));
        return ResponseEntity.ok(userProfileMapper.toResponse(savedUserProfile));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable Long id,
                                                                 @Valid @RequestBody UserProfileRequest request) {
        UserProfile updatedUserProfile = userProfileService.updateUserProfile(id, userProfileMapper.toModel(request));
        return ResponseEntity.ok(userProfileMapper.toResponse(updatedUserProfile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponse>> getAllUserProfiles() {
        List<UserProfileResponse> responses = userProfileMapper.toResponses(userProfileService.getAllUserProfiles());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileMapper.toResponse(userProfileService.getUserProfileById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileMapper.toResponse(userProfileService.getUserProfileByUserId(userId)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserProfile(@PathVariable Long id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.ok("User profile deleted successfully");
    }
}
