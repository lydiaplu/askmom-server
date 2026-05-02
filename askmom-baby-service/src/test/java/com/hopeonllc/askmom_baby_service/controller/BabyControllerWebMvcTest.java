package com.hopeonllc.askmom_baby_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeonllc.askmom_baby_service.exception.GlobalExceptionHandler;
import com.hopeonllc.askmom_baby_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_baby_service.mapper.BabyMapper;
import com.hopeonllc.askmom_baby_service.model.Baby;
import com.hopeonllc.askmom_baby_service.request.BabyRequest;
import com.hopeonllc.askmom_baby_service.response.BabyResponse;
import com.hopeonllc.askmom_baby_service.service.IBabyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

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

@WebMvcTest(BabyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class BabyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IBabyService babyService;

    @MockBean
    private BabyMapper babyMapper;

    @Test
    void addNewBaby_shouldReturnOk_whenValid() throws Exception {
        BabyRequest request = buildValidRequest();
        Baby mapped = buildBaby(5L, 1L);
        BabyResponse response = buildResponse("5", "1");

        when(babyMapper.toModel(any(BabyRequest.class))).thenReturn(mapped);
        when(babyService.addNewBaby(mapped)).thenReturn(mapped);
        when(babyMapper.toResponse(mapped)).thenReturn(response);

        mockMvc.perform(post("/baby/add/new-baby")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.feedingType").value("mixed"));
    }

    @Test
    void addNewBaby_shouldReturnBadRequest_whenBodyValidationFails() throws Exception {
        BabyRequest request = new BabyRequest();
        request.setFeedingType("wrong_type");

        mockMvc.perform(post("/baby/add/new-baby")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DATA_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getBabyById_shouldReturnOk_whenBabyExists() throws Exception {
        Baby baby = buildBaby(5L, 1L);
        BabyResponse response = buildResponse("5", "1");

        when(babyService.getBabyById(5L)).thenReturn(baby);
        when(babyMapper.toResponse(baby)).thenReturn(response);

        mockMvc.perform(get("/baby/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.userId").value("1"))
                .andExpect(jsonPath("$.feedingType").value("mixed"));
    }

    @Test
    void getBabiesByUserId_shouldReturnNotFound_whenMissing() throws Exception {
        when(babyService.getBabiesByUserId(999L))
                .thenThrow(new ResourceNotFoundException("Baby not found with user id: 999"));

        mockMvc.perform(get("/baby/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Baby not found with user id: 999"));
    }

    @Test
    void getBabiesByUserId_shouldReturnOk_whenExists() throws Exception {
        Baby baby = buildBaby(5L, 1L);
        BabyResponse response = buildResponse("5", "1");

        when(babyService.getBabiesByUserId(1L)).thenReturn(List.of(baby));
        when(babyMapper.toResponses(List.of(baby))).thenReturn(List.of(response));

        mockMvc.perform(get("/baby/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("5"))
                .andExpect(jsonPath("$[0].userId").value("1"));
    }

    @Test
    void deleteBaby_shouldReturnOk_whenDeleted() throws Exception {
        doNothing().when(babyService).deleteBaby(7L);

        mockMvc.perform(delete("/baby/delete/7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Baby deleted successfully"));
    }

    private BabyRequest buildValidRequest() {
        BabyRequest request = new BabyRequest();
        request.setUserId(1L);
        request.setFirstName("Amy");
        request.setBirthDate(LocalDate.of(2026, 12, 1));
        request.setFeedingType("mixed");
        return request;
    }

    private Baby buildBaby(Long id, Long userId) {
        Baby baby = new Baby();
        baby.setId(id);
        baby.setUserId(userId);
        baby.setFirstName("Amy");
        baby.setBirthDate(LocalDate.of(2026, 12, 1));
        baby.setFeedingType(Baby.FeedingType.mixed);
        return baby;
    }

    private BabyResponse buildResponse(String id, String userId) {
        BabyResponse response = new BabyResponse();
        response.setId(id);
        response.setUserId(userId);
        response.setFirstName("Amy");
        response.setBirthDate("2026-12-01");
        response.setFeedingType("mixed");
        return response;
    }
}
