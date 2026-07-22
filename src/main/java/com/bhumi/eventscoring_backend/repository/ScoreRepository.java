package com.bhumi.eventscoring_backend.repository;

import com.bhumi.eventscoring_backend.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}