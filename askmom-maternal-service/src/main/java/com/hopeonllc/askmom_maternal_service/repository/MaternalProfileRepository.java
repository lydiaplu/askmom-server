package com.hopeonllc.askmom_maternal_service.repository;

import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaternalProfileRepository extends JpaRepository<MaternalProfile, Long> {

    Optional<MaternalProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
