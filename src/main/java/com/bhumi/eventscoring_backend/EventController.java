package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.dto.CategoryRequest;
import com.bhumi.eventscoring_backend.dto.EventRequest;
import com.bhumi.eventscoring_backend.model.Category;
import com.bhumi.eventscoring_backend.model.Event;
import com.bhumi.eventscoring_backend.model.User;
import com.bhumi.eventscoring_backend.repository.CategoryRepository;
import com.bhumi.eventscoring_backend.repository.EventRepository;
import com.bhumi.eventscoring_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        // @RequestBody converts incoming JSON into an EventRequest object automatically.
        // Authentication contains details of the currently logged-in user provided by Spring Security.

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

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping("/{id}/categories")
    // Maps this method to POST /api/events/{id}/categories for adding a category to a specific event.
    // @PathVariable extracts the event ID from the URL.
    // Finds the event with the given ID and returns it as an Optional.
    // Optional may contain a value or be empty, preventing NullPointerException.

    public Category addCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isEmpty()) {
            throw new RuntimeException("Event not found");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setEvent(eventOpt.get());

        return categoryRepository.save(category);
    }
}
