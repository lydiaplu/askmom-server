package com.hopeonllc.askmom_maternal_service.controller;

import com.hopeonllc.askmom_maternal_service.mapper.MaternalProfileMapper;
import com.hopeonllc.askmom_maternal_service.model.MaternalProfile;
import com.hopeonllc.askmom_maternal_service.request.MaternalProfileRequest;
import com.hopeonllc.askmom_maternal_service.response.MaternalProfileResponse;
import com.hopeonllc.askmom_maternal_service.service.IMaternalProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maternal-profile")
public class MaternalProfileController {

    private final IMaternalProfileService maternalProfileService;
    private final MaternalProfileMapper maternalProfileMapper;

    @PostMapping("/add/new-maternal-profile")
    public ResponseEntity<MaternalProfileResponse> addNewMaternalProfile(@Valid @RequestBody MaternalProfileRequest request) {
        MaternalProfile savedMaternalProfile = maternalProfileService.addNewMaternalProfile(maternalProfileMapper.toModel(request));
        return ResponseEntity.ok(maternalProfileMapper.toResponse(savedMaternalProfile));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MaternalProfileResponse> updateMaternalProfile(@PathVariable Long id,
                                                                         @Valid @RequestBody MaternalProfileRequest request) {
        MaternalProfile updatedMaternalProfile = maternalProfileService.updateMaternalProfile(id, maternalProfileMapper.toModel(request));
        return ResponseEntity.ok(maternalProfileMapper.toResponse(updatedMaternalProfile));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MaternalProfileResponse>> getAllMaternalProfiles() {
        List<MaternalProfileResponse> responses = maternalProfileMapper.toResponses(maternalProfileService.getAllMaternalProfiles());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaternalProfileResponse> getMaternalProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(maternalProfileMapper.toResponse(maternalProfileService.getMaternalProfileById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MaternalProfileResponse> getMaternalProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(maternalProfileMapper.toResponse(maternalProfileService.getMaternalProfileByUserId(userId)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaternalProfile(@PathVariable Long id) {
        maternalProfileService.deleteMaternalProfile(id);
        return ResponseEntity.ok("Maternal profile deleted successfully");
    }
}
