package com.hopeonllc.askmom_user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hopeonllc.askmom_user_service.exception.GlobalExceptionHandler;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.request.LoginRequest;
import com.hopeonllc.askmom_user_service.request.UserRequest;
import com.hopeonllc.askmom_user_service.response.UserResponse;
import com.hopeonllc.askmom_user_service.security.jwt.JwtUtils;
import com.hopeonllc.askmom_user_service.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void addNewUser_shouldReturnConflict_whenServiceThrowsUserAlreadyExists() throws Exception {
        UserRequest request = buildValidUserRequest();
        User mappedUser = new User();
        when(userMapper.toModel(any(UserRequest.class))).thenReturn(mappedUser);
        when(userService.addNewUser(mappedUser)).thenThrow(new UserAlreadyExistsException("Email already exists: mvc@test.com"));

        mockMvc.perform(post("/user/add/new-user")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("Email already exists: mvc@test.com"))
                .andExpect(jsonPath("$.path").value("/user/add/new-user"));
    }

    @Test
    void addNewUser_shouldReturnBadRequest_whenBodyValidationFails() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("bad-email");
        request.setPasswordHash("");

        mockMvc.perform(post("/user/add/new-user")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DATA_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateUser_shouldReturnNotFound_whenServiceThrowsResourceNotFound() throws Exception {
        UserRequest request = buildValidUserRequest();
        User mappedUser = new User();
        when(userMapper.toModel(any(UserRequest.class))).thenReturn(mappedUser);
        when(userService.updateUser(404L, mappedUser)).thenThrow(new ResourceNotFoundException("User not found with id: 404"));

        mockMvc.perform(put("/user/update/404")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("User not found with id: 404"));
    }

    @Test
    void getUserByEmail_shouldReturnBadRequest_whenPathVariableValidationFails() throws Exception {
        mockMvc.perform(get("/user/email/not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DATA_VALIDATION_ERROR"));
    }

    @Test
    void getUserById_shouldReturnInternalServerError_whenUnexpectedExceptionBubblesUp() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("UNEXPECTED_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void login_shouldReturnUnauthorized_whenAuthenticationFails() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void login_shouldReturnToken_whenAuthenticationSucceeds() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("correct-password");
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("user@test.com", null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtTokenForUser(authentication)).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void getUserById_shouldReturnOk_whenUserExists() throws Exception {
        User user = new User();
        user.setId(7L);
        user.setEmail("ok@test.com");
        UserResponse response = new UserResponse();
        response.setId("7");
        response.setEmail("ok@test.com");
        when(userService.getUserById(7L)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/user/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("7"))
                .andExpect(jsonPath("$.email").value("ok@test.com"));
    }

    private UserRequest buildValidUserRequest() {
        UserRequest request = new UserRequest();
        request.setEmail("mvc@test.com");
        request.setPasswordHash("PlainPassword123");
        request.setAuthProvider("email");
        return request;
    }
}
