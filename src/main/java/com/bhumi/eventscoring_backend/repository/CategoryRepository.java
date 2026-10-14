package com.bhumi.eventscoring_backend.repository;

import com.bhumi.eventscoring_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
