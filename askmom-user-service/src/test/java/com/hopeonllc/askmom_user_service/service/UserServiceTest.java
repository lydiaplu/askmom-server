package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.exception.DataValidationException;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void addNewUser_shouldSaveUser_whenEmailValidAndNotExists() {
        User request = buildUser(1L, "alice@test.com");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> {
            User source = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setEmail(source.getEmail());
            target.setPasswordHash(source.getPasswordHash());
            return null;
        }).when(userMapper).updateModel(any(User.class), any(User.class));
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2b$encoded");

        User result = userService.addNewUser(request);

        assertEquals("alice@test.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addNewUser_shouldThrowDataValidationException_whenEmailEmpty() {
        User request = buildUser(1L, " ");

        assertThrows(DataValidationException.class, () -> userService.addNewUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addNewUser_shouldThrowUserAlreadyExistsException_whenEmailExists() {
        User request = buildUser(1L, "alice@test.com");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.addNewUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(99L, buildUser(null, "x@test.com")));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowUserAlreadyExistsException_whenEmailBelongsToAnotherUser() {
        User existingUser = buildUser(1L, "old@test.com");
        User anotherUserWithSameEmail = buildUser(2L, "dup@test.com");
        User updateRequest = buildUser(null, "dup@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(anotherUserWithSameEmail));

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        User user = buildUser(10L, "user@test.com");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(10L);

        assertEquals(10L, result.getId());
        assertEquals("user@test.com", result.getEmail());
    }

    @Test
    void getUserByEmail_shouldThrowResourceNotFoundException_whenNotExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("none@test.com"));
    }

    @Test
    void deleteUser_shouldDelete_whenExists() {
        when(userRepository.existsById(5L)).thenReturn(true);

        userService.deleteUser(5L);

        verify(userRepository).deleteById(5L);
    }

    @Test
    void existsUserByEmail_shouldReturnRepositoryValue() {
        when(userRepository.existsByEmail("check@test.com")).thenReturn(true);

        assertTrue(userService.existsUserByEmail("check@test.com"));

        when(userRepository.existsByEmail("none@test.com")).thenReturn(false);
        assertFalse(userService.existsUserByEmail("none@test.com"));
    }

    @Test
    void getAllUsers_shouldReturnRepositoryResult() {
        when(userRepository.findAll()).thenReturn(List.of(buildUser(1L, "a@test.com"), buildUser(2L, "b@test.com")));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    private User buildUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash("hashed");
        return user;
    }
}
