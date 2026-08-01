package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.model.Category;
import com.bhumi.eventscoring_backend.model.Participant;
import com.bhumi.eventscoring_backend.model.User;
import com.bhumi.eventscoring_backend.repository.CategoryRepository;
import com.bhumi.eventscoring_backend.repository.ParticipantRepository;
import com.bhumi.eventscoring_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{id}/register")
    public Participant register(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();

        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(userEmail))
                .findFirst()
                .orElseThrow();

        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Participant participant = new Participant();
        participant.setUser(user);
        participant.setCategory(categoryOpt.get());

        return participantRepository.save(participant);
    }
}