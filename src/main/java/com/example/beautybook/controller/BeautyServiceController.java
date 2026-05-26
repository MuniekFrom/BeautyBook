package com.example.beautybook.controller;

import com.example.beautybook.dto.CreateBeautyServiceRequest;
import com.example.beautybook.model.BeautyService;
import com.example.beautybook.service.BeautyServiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BeautyServiceController {

    private final BeautyServiceService beautyServiceService;

    public BeautyServiceController(BeautyServiceService beautyServiceService) {
        this.beautyServiceService = beautyServiceService;
    }

    @GetMapping("/services")
    public ResponseEntity<?> getAllServices() {
        return ResponseEntity.ok(beautyServiceService.getAllServices());
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<BeautyService> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(beautyServiceService.getServiceById(id));
    }

    @GetMapping("/services/category/{categoryId}")
    public ResponseEntity<?> getServicesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(beautyServiceService.getServicesByCategoryId(categoryId));
    }

    @PostMapping("/admin/services")
    public ResponseEntity<BeautyService> createService(@Valid @RequestBody CreateBeautyServiceRequest request) {
        BeautyService createdService = beautyServiceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/admin/services/{id}")
    public ResponseEntity<BeautyService> updateService(
            @PathVariable Long id,
            @Valid @RequestBody CreateBeautyServiceRequest request
    ) {
        BeautyService updatedService = beautyServiceService.updateService(id, request);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/admin/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        beautyServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}