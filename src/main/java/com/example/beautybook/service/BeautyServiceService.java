package com.example.beautybook.service;

import com.example.beautybook.dto.CreateBeautyServiceRequest;
import com.example.beautybook.exception.CategoryNotFoundException;
import com.example.beautybook.exception.ServiceNotFoundException;
import com.example.beautybook.model.BeautyCategory;
import com.example.beautybook.model.BeautyService;
import com.example.beautybook.repository.BeautyCategoryRepository;
import com.example.beautybook.repository.BeautyServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeautyServiceService {

    private final BeautyServiceRepository beautyServiceRepository;
    private final BeautyCategoryRepository beautyCategoryRepository;

    public BeautyServiceService(BeautyServiceRepository beautyServiceRepository,
                                BeautyCategoryRepository beautyCategoryRepository) {
        this.beautyServiceRepository = beautyServiceRepository;
        this.beautyCategoryRepository = beautyCategoryRepository;
    }

    public List<BeautyService> getAllServices() {
        return beautyServiceRepository.findAll();
    }

    public BeautyService getServiceById(Long id) {
        return beautyServiceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Service not found with id: " + id
                ));
    }

    public List<BeautyService> getServicesByCategoryId(Long categoryId) {
        return beautyServiceRepository.findByCategoryId(categoryId);
    }

    public BeautyService createService(CreateBeautyServiceRequest request) {

        BeautyCategory category = beautyCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        BeautyService beautyService = new BeautyService(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getDurationMinutes(),
                category
        );

        return beautyServiceRepository.save(beautyService);
    }

    public BeautyService updateService(Long id, CreateBeautyServiceRequest request) {

        BeautyService beautyService = beautyServiceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Service not found with id: " + id
                ));

        BeautyCategory category = beautyCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + request.getCategoryId()
                ));

        beautyService.setName(request.getName());
        beautyService.setDescription(request.getDescription());
        beautyService.setPrice(request.getPrice());
        beautyService.setDurationMinutes(request.getDurationMinutes());
        beautyService.setCategory(category);

        return beautyServiceRepository.save(beautyService);
    }

    public void deleteService(Long id) {

        BeautyService beautyService = beautyServiceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(
                        "Service not found with id: " + id
                ));

        beautyServiceRepository.delete(beautyService);
    }
}