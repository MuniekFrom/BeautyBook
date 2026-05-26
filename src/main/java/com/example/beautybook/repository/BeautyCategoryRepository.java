package com.example.beautybook.repository;

import com.example.beautybook.model.BeautyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeautyCategoryRepository extends JpaRepository<BeautyCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<BeautyCategory> findByNameIgnoreCase(String name);
}