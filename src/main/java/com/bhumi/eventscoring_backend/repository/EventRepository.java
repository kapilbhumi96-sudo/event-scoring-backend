package com.bhumi.eventscoring_backend.repository;

import com.bhumi.eventscoring_backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
