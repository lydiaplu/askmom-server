package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.exception.DataValidationException;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserProfileAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserProfileMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.model.UserProfile;
import com.hopeonllc.askmom_user_service.repository.UserProfileRepository;
import com.hopeonllc.askmom_user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void addNewUserProfile_shouldCreate_whenValid() {
        UserProfile request = buildProfile(1L, "Amy", "Lee");

        when(userProfileRepository.existsByUser_Id(1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile value = invocation.getArgument(0);
            value.setId(100L);
            return value;
        });
        doAnswer(invocation -> {
            UserProfile source = invocation.getArgument(0);
            UserProfile target = invocation.getArgument(1);
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setPreferredLanguage(source.getPreferredLanguage());
            return null;
        }).when(userProfileMapper).updateModel(any(UserProfile.class), any(UserProfile.class));

        UserProfile result = userProfileService.addNewUserProfile(request);

        assertEquals(100L, result.getId());
        assertEquals(1L, result.getUser().getId());
        assertEquals("Amy", result.getFirstName());
    }

    @Test
    void addNewUserProfile_shouldThrow_whenUserIdMissing() {
        UserProfile request = new UserProfile();

        assertThrows(DataValidationException.class, () -> userProfileService.addNewUserProfile(request));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void addNewUserProfile_shouldThrow_whenProfileAlreadyExists() {
        UserProfile request = buildProfile(1L, "Amy", "Lee");
        when(userProfileRepository.existsByUser_Id(1L)).thenReturn(true);

        assertThrows(UserProfileAlreadyExistsException.class, () -> userProfileService.addNewUserProfile(request));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void updateUserProfile_shouldThrow_whenProfileNotFound() {
        UserProfile request = buildProfile(1L, "Amy", "Lee");
        when(userProfileRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.updateUserProfile(10L, request));
    }

    @Test
    void updateUserProfile_shouldThrow_whenTargetUserHasAnotherProfile() {
        UserProfile existing = buildProfileWithId(20L, 1L, "Amy", "Lee");
        UserProfile request = buildProfile(2L, "Amy", "Lee");
        when(userProfileRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(userProfileRepository.existsByUser_Id(2L)).thenReturn(true);

        assertThrows(UserProfileAlreadyExistsException.class, () -> userProfileService.updateUserProfile(20L, request));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void getUserProfileByUserId_shouldReturn_whenExists() {
        UserProfile profile = buildProfileWithId(30L, 1L, "Amy", "Lee");
        when(userProfileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));

        UserProfile result = userProfileService.getUserProfileByUserId(1L);

        assertEquals(30L, result.getId());
        assertEquals(1L, result.getUser().getId());
    }

    @Test
    void deleteUserProfile_shouldDelete_whenExists() {
        when(userProfileRepository.existsById(50L)).thenReturn(true);

        userProfileService.deleteUserProfile(50L);

        verify(userProfileRepository).deleteById(50L);
    }

    @Test
    void getAllUserProfiles_shouldReturnList() {
        when(userProfileRepository.findAll()).thenReturn(List.of(
                buildProfileWithId(1L, 1L, "A", "L"),
                buildProfileWithId(2L, 2L, "B", "M")
        ));

        List<UserProfile> result = userProfileService.getAllUserProfiles();

        assertEquals(2, result.size());
    }

    private UserProfile buildProfile(Long userId, String firstName, String lastName) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(buildUser(userId));
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setPreferredLanguage("EN");
        return userProfile;
    }

    private UserProfile buildProfileWithId(Long id, Long userId, String firstName, String lastName) {
        UserProfile userProfile = buildProfile(userId, firstName, lastName);
        userProfile.setId(id);
        return userProfile;
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
