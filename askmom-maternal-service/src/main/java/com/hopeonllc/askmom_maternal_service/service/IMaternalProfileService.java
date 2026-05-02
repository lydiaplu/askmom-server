package com.hopeonllc.askmom_maternal_service.service;

import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;

import java.util.List;

public interface IMaternalProfileService {

    MaternalProfile addNewMaternalProfile(MaternalProfile requestMaternalProfile);

    MaternalProfile updateMaternalProfile(Long id, MaternalProfile requestMaternalProfile);

    List<MaternalProfile> getAllMaternalProfiles();

    MaternalProfile getMaternalProfileById(Long id);

    MaternalProfile getMaternalProfileByUserId(Long userId);

    void deleteMaternalProfile(Long id);
}
