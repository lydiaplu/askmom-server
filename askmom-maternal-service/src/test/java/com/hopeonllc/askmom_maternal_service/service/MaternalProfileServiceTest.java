package com.hopeonllc.askmom_maternal_service.service;

import com.hopeonllc.askmom_maternal_service.exception.DataValidationException;
import com.hopeonllc.askmom_maternal_service.exception.MaternalProfileAlreadyExistsException;
import com.hopeonllc.askmom_maternal_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_maternal_service.mapper.MaternalProfileMapper;
import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import com.hopeonllc.askmom_maternal_service.repository.MaternalProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
class MaternalProfileServiceTest {

    @Mock
    private MaternalProfileRepository maternalProfileRepository;

    @Mock
    private MaternalProfileMapper maternalProfileMapper;

    @InjectMocks
    private MaternalProfileService maternalProfileService;

    @Test
    void addNewMaternalProfile_shouldCreate_whenValid() {
        MaternalProfile request = buildProfile(1L);

        when(maternalProfileRepository.existsByUserId(1L)).thenReturn(false);
        when(maternalProfileRepository.save(any(MaternalProfile.class))).thenAnswer(invocation -> {
            MaternalProfile value = invocation.getArgument(0);
            value.setId(100L);
            return value;
        });
        doAnswer(invocation -> {
            MaternalProfile source = invocation.getArgument(0);
            MaternalProfile target = invocation.getArgument(1);
            target.setUserId(source.getUserId());
            target.setDueDate(source.getDueDate());
            target.setBabyCount(source.getBabyCount());
            target.setMaternalStage(source.getMaternalStage());
            return null;
        }).when(maternalProfileMapper).updateModel(any(MaternalProfile.class), any(MaternalProfile.class));

        MaternalProfile result = maternalProfileService.addNewMaternalProfile(request);

        assertEquals(100L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("pregnant", result.getMaternalStage());
    }

    @Test
    void addNewMaternalProfile_shouldThrow_whenUserIdMissing() {
        MaternalProfile request = new MaternalProfile();

        assertThrows(DataValidationException.class, () -> maternalProfileService.addNewMaternalProfile(request));
        verify(maternalProfileRepository, never()).save(any(MaternalProfile.class));
    }

    @Test
    void addNewMaternalProfile_shouldThrow_whenProfileAlreadyExists() {
        MaternalProfile request = buildProfile(1L);
        when(maternalProfileRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(MaternalProfileAlreadyExistsException.class, () -> maternalProfileService.addNewMaternalProfile(request));
        verify(maternalProfileRepository, never()).save(any(MaternalProfile.class));
    }

    @Test
    void updateMaternalProfile_shouldThrow_whenProfileNotFound() {
        MaternalProfile request = buildProfile(1L);
        when(maternalProfileRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> maternalProfileService.updateMaternalProfile(10L, request));
    }

    @Test
    void updateMaternalProfile_shouldThrow_whenTargetUserHasAnotherProfile() {
        MaternalProfile existing = buildProfileWithId(20L, 1L);
        MaternalProfile request = buildProfile(2L);
        when(maternalProfileRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(maternalProfileRepository.existsByUserId(2L)).thenReturn(true);

        assertThrows(MaternalProfileAlreadyExistsException.class, () -> maternalProfileService.updateMaternalProfile(20L, request));
        verify(maternalProfileRepository, never()).save(any(MaternalProfile.class));
    }

    @Test
    void getMaternalProfileByUserId_shouldReturn_whenExists() {
        MaternalProfile profile = buildProfileWithId(30L, 1L);
        when(maternalProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        MaternalProfile result = maternalProfileService.getMaternalProfileByUserId(1L);

        assertEquals(30L, result.getId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void deleteMaternalProfile_shouldDelete_whenExists() {
        when(maternalProfileRepository.existsById(50L)).thenReturn(true);

        maternalProfileService.deleteMaternalProfile(50L);

        verify(maternalProfileRepository).deleteById(50L);
    }

    @Test
    void getAllMaternalProfiles_shouldReturnList() {
        when(maternalProfileRepository.findAll()).thenReturn(List.of(
                buildProfileWithId(1L, 1L),
                buildProfileWithId(2L, 2L)
        ));

        List<MaternalProfile> result = maternalProfileService.getAllMaternalProfiles();

        assertEquals(2, result.size());
    }

    private MaternalProfile buildProfile(Long userId) {
        MaternalProfile maternalProfile = new MaternalProfile();
        maternalProfile.setUserId(userId);
        maternalProfile.setDueDate(LocalDate.of(2026, 12, 1));
        maternalProfile.setBabyCount(1);
        maternalProfile.setMaternalStage("pregnant");
        return maternalProfile;
    }

    private MaternalProfile buildProfileWithId(Long id, Long userId) {
        MaternalProfile maternalProfile = buildProfile(userId);
        maternalProfile.setId(id);
        return maternalProfile;
    }
}
