package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.dto.CategoryView;
import com.bhumi.eventscoring_backend.dto.EventRequest;
import com.bhumi.eventscoring_backend.model.Category;
import com.bhumi.eventscoring_backend.model.Event;
import com.bhumi.eventscoring_backend.model.User;
import com.bhumi.eventscoring_backend.repository.CategoryRepository;
import com.bhumi.eventscoring_backend.repository.EventRepository;
import com.bhumi.eventscoring_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Event createEvent(@RequestBody EventRequest request, Authentication authentication) {
        String organizerEmail = authentication.getName();
        User organizer = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(organizerEmail))
                .findFirst()
                .orElseThrow();

        Event event = new Event();
        event.setName(request.getName());
        event.setDate(request.getDate());
        event.setOrganizer(organizer);

        return eventRepository.save(event);
    }

    @GetMapping("/{id}/categories")
    public List<CategoryView> getCategories(@PathVariable Long id) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getEvent().getId().equals(id))
                .map(c -> new CategoryView(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Maps this method to POST /api/events/{id}/categories for adding a category to a specific event.
    @PostMapping("/{id}/categories")
    public ResponseEntity<?> addCategory(@PathVariable Long id, @RequestBody Category category) {
        Event event = eventRepository.findById(id).orElseThrow();

        boolean exists = categoryRepository.existsByEventIdAndNameIgnoreCase(id, category.getName());
        if (exists) {
            return ResponseEntity.badRequest().body("A category with this name already exists for this event");
        }

        category.setEvent(event);
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(saved);
    }
}