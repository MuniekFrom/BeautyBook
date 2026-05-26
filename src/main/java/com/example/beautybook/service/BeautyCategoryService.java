package com.example.beautybook.service;

import com.example.beautybook.dto.BeautyCategoryRequest;
import com.example.beautybook.dto.BeautyCategoryResponse;
import com.example.beautybook.exception.CategoryAlreadyExistsException;
import com.example.beautybook.exception.CategoryNotFoundException;
import com.example.beautybook.model.BeautyCategory;
import com.example.beautybook.repository.BeautyCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeautyCategoryService {

    private final BeautyCategoryRepository beautyCategoryRepository;

    public BeautyCategoryService(BeautyCategoryRepository beautyCategoryRepository) {
        this.beautyCategoryRepository = beautyCategoryRepository;
    }

    public List<BeautyCategoryResponse> getAllCategories() {
        return beautyCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BeautyCategoryResponse createCategory(BeautyCategoryRequest request) {
        String categoryName = request.getName().trim();

        if (beautyCategoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new CategoryAlreadyExistsException("Category already exists: " + categoryName);
        }

        BeautyCategory category = new BeautyCategory(categoryName);
        BeautyCategory savedCategory = beautyCategoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    public void deleteCategory(Long id) {
        BeautyCategory category = beautyCategoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        beautyCategoryRepository.delete(category);
    }

    private BeautyCategoryResponse mapToResponse(BeautyCategory category) {
        return new BeautyCategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}