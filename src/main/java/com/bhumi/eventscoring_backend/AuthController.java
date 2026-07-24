package com.bhumi.eventscoring_backend;

import com.bhumi.eventscoring_backend.dto.RegisterRequest;
import com.bhumi.eventscoring_backend.model.User;
import com.bhumi.eventscoring_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bhumi.eventscoring_backend.dto.LoginRequest;
import java.util.Optional;

@RestController                             // handles web request
@RequestMapping("/api/auth")               // prefix for all
@CrossOrigin(origins = "http://localhost:5173")     // so the request is not blocked by browser
public class AuthController {

    @Autowired                                  //  hands the previously created instance directly
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")         // when someone sends a POST request to /register run the method below
    public String register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // password encrypted
        user.setRole(request.getRole());

        userRepository.save(user);
        return "User registered successfully";
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(request.getEmail()))
                .findFirst();

        if (userOpt.isEmpty()) {
            return "User not found";
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Incorrect password";
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }
}
