package com.hopeonllc.askmom_baby_service.controller;

import com.hopeonllc.askmom_baby_service.mapper.BabyMapper;
import com.hopeonllc.askmom_baby_service.model.Baby;
import com.hopeonllc.askmom_baby_service.request.BabyRequest;
import com.hopeonllc.askmom_baby_service.response.BabyResponse;
import com.hopeonllc.askmom_baby_service.service.IBabyService;
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
@RequestMapping("/baby")
public class BabyController {

    private final IBabyService babyService;
    private final BabyMapper babyMapper;

    @PostMapping("/add/new-baby")
    public ResponseEntity<BabyResponse> addNewBaby(@Valid @RequestBody BabyRequest request) {
        Baby savedBaby = babyService.addNewBaby(babyMapper.toModel(request));
        return ResponseEntity.ok(babyMapper.toResponse(savedBaby));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BabyResponse> updateBaby(@PathVariable Long id,
                                                   @Valid @RequestBody BabyRequest request) {
        Baby updatedBaby = babyService.updateBaby(id, babyMapper.toModel(request));
        return ResponseEntity.ok(babyMapper.toResponse(updatedBaby));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BabyResponse>> getAllBabies() {
        List<BabyResponse> responses = babyMapper.toResponses(babyService.getAllBabies());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BabyResponse> getBabyById(@PathVariable Long id) {
        return ResponseEntity.ok(babyMapper.toResponse(babyService.getBabyById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BabyResponse>> getBabiesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(babyMapper.toResponses(babyService.getBabiesByUserId(userId)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBaby(@PathVariable Long id) {
        babyService.deleteBaby(id);
        return ResponseEntity.ok("Baby deleted successfully");
    }
}
