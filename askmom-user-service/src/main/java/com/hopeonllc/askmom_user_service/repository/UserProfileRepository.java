package com.hopeonllc.askmom_user_service.repository;

import com.hopeonllc.askmom_user_service.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);
}
