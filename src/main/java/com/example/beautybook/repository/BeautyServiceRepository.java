package com.example.beautybook.repository;

import com.example.beautybook.model.BeautyService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

    List<BeautyService> findByCategoryId(Long categoryId);
}