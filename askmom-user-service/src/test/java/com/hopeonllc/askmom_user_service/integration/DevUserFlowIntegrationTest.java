package com.hopeonllc.askmom_user_service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.repository.UserRepository;
import com.hopeonllc.askmom_user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class DevUserFlowIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userCrudFlow_shouldPersistUpdateAndDeleteAgainstDevDatabase() {
        String initialEmail = uniqueEmail("crud-create");
        User createRequest = new User();
        createRequest.setEmail(initialEmail);
        createRequest.setPasswordHash("PlainPassword123");

        User savedUser = userService.addNewUser(createRequest);

        assertNotNull(savedUser.getId());
        assertTrue(savedUser.getPasswordHash().startsWith("$2"));
        assertTrue(userRepository.existsByEmail(initialEmail));
        assertEquals(initialEmail, userService.getUserByEmail(initialEmail).getEmail());

        String updatedEmail = uniqueEmail("crud-update");
        User updateRequest = new User();
        updateRequest.setEmail(updatedEmail);
        updateRequest.setPasswordHash(savedUser.getPasswordHash());
        User updatedUser = userService.updateUser(savedUser.getId(), updateRequest);

        assertEquals(updatedEmail, updatedUser.getEmail());
        assertEquals(updatedEmail, userService.getUserById(savedUser.getId()).getEmail());

        userService.deleteUser(savedUser.getId());

        assertTrue(userRepository.findById(savedUser.getId()).isEmpty());
    }

    @Test
    void repository_shouldEnforceUniqueEmailConstraint() {
        String email = uniqueEmail("unique");
        userRepository.saveAndFlush(buildPersistedUser(email));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(buildPersistedUser(email));
        });
    }

    @Test
    void loginAndProtectedEndpoint_shouldWorkWithJwtInDevProfile() throws Exception {
        String email = uniqueEmail("login");
        String rawPassword = "Password123!";
        userRepository.saveAndFlush(buildPersistedUser(email, rawPassword));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, rawPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String token = jsonNode.get("accessToken").asText();

        mockMvc.perform(get("/user/exists/email/{email}", email)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value("true"));
    }

    @Test
    void protectedEndpoint_shouldRejectInvalidJwt() throws Exception {
        mockMvc.perform(get("/user/1")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    private User buildPersistedUser(String email) {
        return buildPersistedUser(email, "Password123!");
    }

    private User buildPersistedUser(String email, String rawPassword) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setActive(true);
        user.setAuthProvider("email");
        return user;
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@test.com";
    }
}
