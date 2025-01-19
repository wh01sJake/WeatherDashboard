package com.Likun.weatherdashboard.controller;

import com.Likun.weatherdashboard.model.FavoriteCity;
import com.Likun.weatherdashboard.model.User;
import com.Likun.weatherdashboard.repository.UserRepository;
import com.Likun.weatherdashboard.service.FavoriteCityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteCityController {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteCityController.class);

    @Autowired
    private FavoriteCityService favoriteCityService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/favorites - Retrieves favorite cities for an authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<FavoriteCity>> getFavorites() {
        String username = getAuthenticatedUsername();
        User user = fetchUser(username);

        logger.debug("Fetching favorite cities for user: {}", username);
        List<FavoriteCity> favorites = favoriteCityService.getFavoritesByUser(user);

        return ResponseEntity.ok(favorites);
    }

    /**
     * POST /api/favorites - Add a favorite city for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<FavoriteCity> addFavorite(@RequestParam String city) {
        String username = getAuthenticatedUsername();
        User user = fetchUser(username);

        logger.debug("Adding favorite city '{}' for user: {}", city, username);
        FavoriteCity favoriteCity = favoriteCityService.addFavoriteCity(city, user);

        return ResponseEntity.status(201).body(favoriteCity);
    }

    /**
     * DELETE /api/favorites - Remove a favorite city for the authenticated user.
     */
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(@RequestParam String city) {
        String username = getAuthenticatedUsername();
        User user = fetchUser(username);

        logger.debug("Removing favorite city '{}' for user: {}", city, username);
        favoriteCityService.removeFavoriteCity(city, user);

        return ResponseEntity.noContent().build(); // 204 - No Content
    }

    // Utility method to get the authenticated username
    private String getAuthenticatedUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Authentication failed, no user authenticated.");
            throw new RuntimeException("Unauthorized user!");
        }
        return authentication.getName();
    }

    // Utility method to fetch the User object from the repository
    private User fetchUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User '{}' not found in database!", username);
                    return new RuntimeException("User not found: " + username);
                });
    }
}