package com.hopeonllc.askmom_user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeonllc.askmom_user_service.exception.GlobalExceptionHandler;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserProfileAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserProfileMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.model.UserProfile;
import com.hopeonllc.askmom_user_service.request.UserProfileRequest;
import com.hopeonllc.askmom_user_service.response.UserProfileResponse;
import com.hopeonllc.askmom_user_service.service.IUserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserProfileControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserProfileService userProfileService;

    @MockBean
    private UserProfileMapper userProfileMapper;

    @Test
    void addNewUserProfile_shouldReturnConflict_whenServiceThrowsUserProfileAlreadyExists() throws Exception {
        UserProfileRequest request = buildValidRequest();
        UserProfile mapped = new UserProfile();
        when(userProfileMapper.toModel(any(UserProfileRequest.class))).thenReturn(mapped);
        when(userProfileService.addNewUserProfile(mapped))
                .thenThrow(new UserProfileAlreadyExistsException("User profile already exists for user id: 1"));

        mockMvc.perform(post("/user-profile/add/new-user-profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("USER_PROFILE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("User profile already exists for user id: 1"));
    }

    @Test
    void addNewUserProfile_shouldReturnBadRequest_whenBodyValidationFails() throws Exception {
        UserProfileRequest request = new UserProfileRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setPreferredLanguage("");

        mockMvc.perform(post("/user-profile/add/new-user-profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DATA_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getUserProfileById_shouldReturnOk_whenProfileExists() throws Exception {
        UserProfile profile = buildProfile(5L, 1L);
        UserProfileResponse response = new UserProfileResponse();
        response.setId("5");
        response.setUserId("1");
        response.setFirstName("Amy");
        response.setLastName("Lee");

        when(userProfileService.getUserProfileById(5L)).thenReturn(profile);
        when(userProfileMapper.toResponse(profile)).thenReturn(response);

        mockMvc.perform(get("/user-profile/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.firstName").value("Amy"));
    }

    @Test
    void getUserProfileByUserId_shouldReturnNotFound_whenMissing() throws Exception {
        when(userProfileService.getUserProfileByUserId(999L))
                .thenThrow(new ResourceNotFoundException("User profile not found with user id: 999"));

        mockMvc.perform(get("/user-profile/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User profile not found with user id: 999"));
    }

    @Test
    void deleteUserProfile_shouldReturnOk_whenDeleted() throws Exception {
        doNothing().when(userProfileService).deleteUserProfile(7L);

        mockMvc.perform(delete("/user-profile/delete/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("User profile deleted successfully"));
    }

    private UserProfileRequest buildValidRequest() {
        UserProfileRequest request = new UserProfileRequest();
        request.setUserId(1L);
        request.setFirstName("Amy");
        request.setLastName("Lee");
        request.setPreferredLanguage("EN");
        return request;
    }

    private UserProfile buildProfile(Long id, Long userId) {
        User user = new User();
        user.setId(userId);

        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setUser(user);
        profile.setFirstName("Amy");
        profile.setLastName("Lee");
        profile.setPreferredLanguage("EN");
        return profile;
    }
}
