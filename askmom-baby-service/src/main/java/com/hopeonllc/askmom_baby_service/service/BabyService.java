package com.hopeonllc.askmom_baby_service.service;

import com.hopeonllc.askmom_baby_service.exception.DataValidationException;
import com.hopeonllc.askmom_baby_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_baby_service.mapper.BabyMapper;
import com.hopeonllc.askmom_baby_service.model.Baby;
import com.hopeonllc.askmom_baby_service.repository.BabyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BabyService implements IBabyService {
    private static final Logger log = LoggerFactory.getLogger(BabyService.class);

    private final BabyRepository babyRepository;
    private final BabyMapper babyMapper;

    @Override
    public Baby addNewBaby(Baby requestBaby) {
        extractUserId(requestBaby);

        Baby baby = new Baby();
        babyMapper.updateModel(requestBaby, baby);

        Baby savedBaby = babyRepository.save(baby);
        log.info("Baby created: id={}, userId={}", savedBaby.getId(), savedBaby.getUserId());
        return savedBaby;
    }

    @Override
    public Baby updateBaby(Long id, Baby requestBaby) {
        Baby existingBaby = babyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Baby not found with id: " + id));

        extractUserId(requestBaby);

        babyMapper.updateModel(requestBaby, existingBaby);

        Baby updatedBaby = babyRepository.save(existingBaby);
        log.info("Baby updated: id={}, userId={}", updatedBaby.getId(), updatedBaby.getUserId());
        return updatedBaby;
    }

    @Override
    public List<Baby> getAllBabies() {
        return babyRepository.findAll();
    }

    @Override
    public Baby getBabyById(Long id) {
        return babyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Baby not found with id: " + id));
    }

    @Override
    public List<Baby> getBabiesByUserId(Long userId) {
        List<Baby> babies = babyRepository.findByUserId(userId);
        if (babies.isEmpty()) {
            throw new ResourceNotFoundException("Baby not found with user id: " + userId);
        }
        return babies;
    }

    @Override
    public void deleteBaby(Long id) {
        if (!babyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Baby not found with id: " + id);
        }
        babyRepository.deleteById(id);
        log.info("Baby deleted: id={}", id);
    }

    private Long extractUserId(Baby baby) {
        if (baby == null || baby.getUserId() == null) {
            throw new DataValidationException("User id cannot be empty");
        }
        return baby.getUserId();
    }
}
