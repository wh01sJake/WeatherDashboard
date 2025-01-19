package com.Likun.weatherdashboard.controller;

import com.Likun.weatherdashboard.repository.AuthorityRepository;
import com.Likun.weatherdashboard.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserController(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    /**
     * Fetch Roles for a Specific User (Restricted to Admins or Self)
     */
    @GetMapping("/{username}/roles")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<?> getUserRoles(@PathVariable String username) {
        if (userRepository.findByUsername(username) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found."));
        }

        List<String> roles = authorityRepository.findAuthoritiesByUsername(username);
        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No roles found for user."));
        }
        return ResponseEntity.ok(Map.of("username", username, "roles", roles));
    }

    /**
     * Fetch Roles of Logged-In User
     */
    @GetMapping("/me/roles")
    public ResponseEntity<?> getCurrentUserRoles(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authorityRepository.findAuthoritiesByUsername(username);

        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No roles found for user."));
        }
        return ResponseEntity.ok(Map.of("username", username, "roles", roles));
    }
}