package com.prepforge.controller;

import com.prepforge.entity.User;
import com.prepforge.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;

    public AuthController(UserRepository r, PasswordEncoder e, AuthenticationConfiguration c) throws Exception {
        repo = r;
        encoder = e;
        manager = c.getAuthenticationManager();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> b) {
        String name = b.getOrDefault("name", "").trim();
        String email = b.getOrDefault("email", "").trim().toLowerCase();
        String password = b.getOrDefault("password", "");

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please fill all fields"));
        }
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
        }
        if (repo.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered. Please login."));
        }

        User u = new User(name, email, encoder.encode(password), "STUDENT");
        repo.save(u);
        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> b, HttpSession session) {
        String email = b.getOrDefault("email", "").trim().toLowerCase();
        String password = b.getOrDefault("password", "");

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        try {
            Authentication a = manager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            session.setAttribute("SPRING_SECURITY_CONTEXT",
                new org.springframework.security.core.context.SecurityContextImpl(a));

            User u = repo.findByEmail(email).orElseThrow();
            return ResponseEntity.ok(Map.of(
                "name", u.getName(),
                "role", u.getRole(),
                "redirect", u.getRole().equals("ADMIN")
                    ? "/admin/dashboard.html"
                    : "/student/dashboard.html"
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of(
                "message", "Invalid email or password. Please use the same password you used during registration."
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication a) {
        if (a == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of("email", a.getName()));
    }
}
