package com.hopeonllc.askmom_baby_service.service;

import com.hopeonllc.askmom_baby_service.model.Baby;

import java.util.List;

public interface IBabyService {

    Baby addNewBaby(Baby requestBaby);

    Baby updateBaby(Long id, Baby requestBaby);

    List<Baby> getAllBabies();

    Baby getBabyById(Long id);

    List<Baby> getBabiesByUserId(Long userId);

    void deleteBaby(Long id);
}
