package com.hopeonllc.askmom_maternal_service.service;

import com.hopeonllc.askmom_maternal_service.exception.DataValidationException;
import com.hopeonllc.askmom_maternal_service.exception.MaternalProfileAlreadyExistsException;
import com.hopeonllc.askmom_maternal_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_maternal_service.mapper.MaternalProfileMapper;
import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import com.hopeonllc.askmom_maternal_service.repository.MaternalProfileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaternalProfileService implements IMaternalProfileService {
    private static final Logger log = LoggerFactory.getLogger(MaternalProfileService.class);

    private final MaternalProfileRepository maternalProfileRepository;
    private final MaternalProfileMapper maternalProfileMapper;

    @Override
    public MaternalProfile addNewMaternalProfile(MaternalProfile requestMaternalProfile) {
        Long userId = extractUserId(requestMaternalProfile);
        validateMaternalProfileForCreate(userId);

        MaternalProfile maternalProfile = new MaternalProfile();
        maternalProfileMapper.updateModel(requestMaternalProfile, maternalProfile);

        MaternalProfile savedMaternalProfile = maternalProfileRepository.save(maternalProfile);
        log.info("Maternal profile created: id={}, userId={}", savedMaternalProfile.getId(), savedMaternalProfile.getUserId());
        return savedMaternalProfile;
    }

    @Override
    public MaternalProfile updateMaternalProfile(Long id, MaternalProfile requestMaternalProfile) {
        MaternalProfile existingMaternalProfile = maternalProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maternal profile not found with id: " + id));

        Long requestedUserId = extractUserId(requestMaternalProfile);
        validateMaternalProfileForUpdate(id, existingMaternalProfile, requestedUserId);

        maternalProfileMapper.updateModel(requestMaternalProfile, existingMaternalProfile);

        MaternalProfile updatedMaternalProfile = maternalProfileRepository.save(existingMaternalProfile);
        log.info("Maternal profile updated: id={}, userId={}", updatedMaternalProfile.getId(), updatedMaternalProfile.getUserId());
        return updatedMaternalProfile;
    }

    @Override
    public List<MaternalProfile> getAllMaternalProfiles() {
        return maternalProfileRepository.findAll();
    }

    @Override
    public MaternalProfile getMaternalProfileById(Long id) {
        return maternalProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maternal profile not found with id: " + id));
    }

    @Override
    public MaternalProfile getMaternalProfileByUserId(Long userId) {
        return maternalProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Maternal profile not found with user id: " + userId));
    }

    @Override
    public void deleteMaternalProfile(Long id) {
        if (!maternalProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maternal profile not found with id: " + id);
        }
        maternalProfileRepository.deleteById(id);
        log.info("Maternal profile deleted: id={}", id);
    }

    private void validateMaternalProfileForCreate(Long userId) {
        if (maternalProfileRepository.existsByUserId(userId)) {
            log.warn("Create maternal profile validation failed: maternal profile already exists, userId={}", userId);
            throw new MaternalProfileAlreadyExistsException("Maternal profile already exists for user id: " + userId);
        }
    }

    private void validateMaternalProfileForUpdate(Long profileId, MaternalProfile existingMaternalProfile, Long requestedUserId) {
        Long currentUserId = existingMaternalProfile.getUserId();
        if (!currentUserId.equals(requestedUserId) && maternalProfileRepository.existsByUserId(requestedUserId)) {
            log.warn("Update maternal profile validation failed: target user already has profile, profileId={}, userId={}",
                    profileId, requestedUserId);
            throw new MaternalProfileAlreadyExistsException("Maternal profile already exists for user id: " + requestedUserId);
        }
    }

    private Long extractUserId(MaternalProfile maternalProfile) {
        if (maternalProfile == null || maternalProfile.getUserId() == null) {
            throw new DataValidationException("User id cannot be empty");
        }
        return maternalProfile.getUserId();
    }
}
