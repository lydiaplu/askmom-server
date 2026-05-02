package com.hopeonllc.askmom_baby_service.service;

import com.hopeonllc.askmom_baby_service.exception.DataValidationException;
import com.hopeonllc.askmom_baby_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_baby_service.mapper.BabyMapper;
import com.hopeonllc.askmom_baby_service.model.Baby;
import com.hopeonllc.askmom_baby_service.repository.BabyRepository;
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
class BabyServiceTest {

    @Mock
    private BabyRepository babyRepository;

    @Mock
    private BabyMapper babyMapper;

    @InjectMocks
    private BabyService babyService;

    @Test
    void addNewBaby_shouldCreate_whenValid() {
        Baby request = buildBaby(1L);

        when(babyRepository.save(any(Baby.class))).thenAnswer(invocation -> {
            Baby value = invocation.getArgument(0);
            value.setId(100L);
            return value;
        });
        doAnswer(invocation -> {
            Baby source = invocation.getArgument(0);
            Baby target = invocation.getArgument(1);
            target.setUserId(source.getUserId());
            target.setFirstName(source.getFirstName());
            target.setBirthDate(source.getBirthDate());
            target.setFeedingType(source.getFeedingType());
            return null;
        }).when(babyMapper).updateModel(any(Baby.class), any(Baby.class));

        Baby result = babyService.addNewBaby(request);

        assertEquals(100L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals("Amy", result.getFirstName());
    }

    @Test
    void addNewBaby_shouldThrow_whenUserIdMissing() {
        Baby request = new Baby();

        assertThrows(DataValidationException.class, () -> babyService.addNewBaby(request));
        verify(babyRepository, never()).save(any(Baby.class));
    }

    @Test
    void updateBaby_shouldThrow_whenBabyNotFound() {
        Baby request = buildBaby(1L);
        when(babyRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> babyService.updateBaby(10L, request));
    }

    @Test
    void getBabyById_shouldReturn_whenExists() {
        Baby baby = buildBabyWithId(30L, 1L);
        when(babyRepository.findById(30L)).thenReturn(Optional.of(baby));

        Baby result = babyService.getBabyById(30L);

        assertEquals(30L, result.getId());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getBabiesByUserId_shouldReturn_whenExists() {
        when(babyRepository.findByUserId(1L)).thenReturn(List.of(
                buildBabyWithId(1L, 1L),
                buildBabyWithId(2L, 1L)
        ));

        List<Baby> result = babyService.getBabiesByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getBabiesByUserId_shouldThrow_whenMissing() {
        when(babyRepository.findByUserId(999L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> babyService.getBabiesByUserId(999L));
    }

    @Test
    void deleteBaby_shouldDelete_whenExists() {
        when(babyRepository.existsById(50L)).thenReturn(true);

        babyService.deleteBaby(50L);

        verify(babyRepository).deleteById(50L);
    }

    @Test
    void getAllBabies_shouldReturnList() {
        when(babyRepository.findAll()).thenReturn(List.of(
                buildBabyWithId(1L, 1L),
                buildBabyWithId(2L, 2L)
        ));

        List<Baby> result = babyService.getAllBabies();

        assertEquals(2, result.size());
    }

    private Baby buildBaby(Long userId) {
        Baby baby = new Baby();
        baby.setUserId(userId);
        baby.setFirstName("Amy");
        baby.setBirthDate(LocalDate.of(2026, 12, 1));
        baby.setFeedingType(Baby.FeedingType.mixed);
        return baby;
    }

    private Baby buildBabyWithId(Long id, Long userId) {
        Baby baby = buildBaby(userId);
        baby.setId(id);
        return baby;
    }
}
