package com.example.beautybook.controller;

import com.example.beautybook.dto.BeautyCategoryRequest;
import com.example.beautybook.dto.BeautyCategoryResponse;
import com.example.beautybook.service.BeautyCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BeautyCategoryController {

    private final BeautyCategoryService beautyCategoryService;

    public BeautyCategoryController(BeautyCategoryService beautyCategoryService) {
        this.beautyCategoryService = beautyCategoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(beautyCategoryService.getAllCategories());
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<BeautyCategoryResponse> createCategory(
            @Valid @RequestBody BeautyCategoryRequest request
    ) {
        BeautyCategoryResponse response = beautyCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        beautyCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}