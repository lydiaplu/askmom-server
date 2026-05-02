package com.hopeonllc.askmom_maternal_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeonllc.askmom_maternal_service.exception.GlobalExceptionHandler;
import com.hopeonllc.askmom_maternal_service.exception.MaternalProfileAlreadyExistsException;
import com.hopeonllc.askmom_maternal_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_maternal_service.mapper.MaternalProfileMapper;
import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import com.hopeonllc.askmom_maternal_service.request.MaternalProfileRequest;
import com.hopeonllc.askmom_maternal_service.response.MaternalProfileResponse;
import com.hopeonllc.askmom_maternal_service.service.IMaternalProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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

@WebMvcTest(MaternalProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MaternalProfileControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMaternalProfileService maternalProfileService;

    @MockBean
    private MaternalProfileMapper maternalProfileMapper;

    @Test
    void addNewMaternalProfile_shouldReturnConflict_whenServiceThrowsMaternalProfileAlreadyExists() throws Exception {
        MaternalProfileRequest request = buildValidRequest();
        MaternalProfile mapped = new MaternalProfile();
        when(maternalProfileMapper.toModel(any(MaternalProfileRequest.class))).thenReturn(mapped);
        when(maternalProfileService.addNewMaternalProfile(mapped))
                .thenThrow(new MaternalProfileAlreadyExistsException("Maternal profile already exists for user id: 1"));

        mockMvc.perform(post("/maternal-profile/add/new-maternal-profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("MATERNAL_PROFILE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Maternal profile already exists for user id: 1"));
    }

    @Test
    void addNewMaternalProfile_shouldReturnBadRequest_whenBodyValidationFails() throws Exception {
        MaternalProfileRequest request = new MaternalProfileRequest();
        request.setBabyCount(0);

        mockMvc.perform(post("/maternal-profile/add/new-maternal-profile")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DATA_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMaternalProfileById_shouldReturnOk_whenProfileExists() throws Exception {
        MaternalProfile profile = buildProfile(5L, 1L);
        MaternalProfileResponse response = new MaternalProfileResponse();
        response.setId("5");
        response.setUserId("1");
        response.setMaternalStage("pregnant");

        when(maternalProfileService.getMaternalProfileById(5L)).thenReturn(profile);
        when(maternalProfileMapper.toResponse(profile)).thenReturn(response);

        mockMvc.perform(get("/maternal-profile/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.maternalStage").value("pregnant"));
    }

    @Test
    void getMaternalProfileByUserId_shouldReturnNotFound_whenMissing() throws Exception {
        when(maternalProfileService.getMaternalProfileByUserId(999L))
                .thenThrow(new ResourceNotFoundException("Maternal profile not found with user id: 999"));

        mockMvc.perform(get("/maternal-profile/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Maternal profile not found with user id: 999"));
    }

    @Test
    void deleteMaternalProfile_shouldReturnOk_whenDeleted() throws Exception {
        doNothing().when(maternalProfileService).deleteMaternalProfile(7L);

        mockMvc.perform(delete("/maternal-profile/delete/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Maternal profile deleted successfully"));
    }

    private MaternalProfileRequest buildValidRequest() {
        MaternalProfileRequest request = new MaternalProfileRequest();
        request.setUserId(1L);
        request.setDueDate(LocalDate.of(2026, 12, 1));
        request.setBabyCount(1);
        request.setMaternalStage("pregnant");
        return request;
    }

    private MaternalProfile buildProfile(Long id, Long userId) {
        MaternalProfile profile = new MaternalProfile();
        profile.setId(id);
        profile.setUserId(userId);
        profile.setDueDate(LocalDate.of(2026, 12, 1));
        profile.setBabyCount(1);
        profile.setMaternalStage("pregnant");
        return profile;
    }
}
