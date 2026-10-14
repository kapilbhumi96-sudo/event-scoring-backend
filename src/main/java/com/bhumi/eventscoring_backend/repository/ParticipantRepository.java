package com.bhumi.eventscoring_backend.repository;

import com.bhumi.eventscoring_backend.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
