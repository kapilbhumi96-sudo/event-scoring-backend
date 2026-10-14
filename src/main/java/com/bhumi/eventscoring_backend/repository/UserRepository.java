package com.bhumi.eventscoring_backend.repository;

import com.bhumi.eventscoring_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
