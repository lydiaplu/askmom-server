package com.hopeonllc.askmom_baby_service.repository;

import com.hopeonllc.askmom_baby_service.model.Baby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BabyRepository extends JpaRepository<Baby, Long> {

    List<Baby> findByUserId(Long userId);
}
