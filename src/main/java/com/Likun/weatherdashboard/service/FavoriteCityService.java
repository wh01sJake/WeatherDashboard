package com.Likun.weatherdashboard.service;

import com.Likun.weatherdashboard.model.FavoriteCity;
import com.Likun.weatherdashboard.model.User;
import com.Likun.weatherdashboard.repository.FavoriteCityRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteCityService {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteCityService.class);

    @Autowired
    private FavoriteCityRepository favoriteCityRepository;

    /**
     * Fetch all favorite cities for a given user.
     *
     * @param user - The logged-in user
     * @return List of FavoriteCity entities
     */
    public List<FavoriteCity> getFavoritesByUser(User user) {
        validateUser(user);

        try {
            logger.debug("Fetching favorite cities for user: {}", user.getUsername());
            return favoriteCityRepository.findByUser(user);
        } catch (Exception e) {
            logger.error("Error fetching favorite cities for user '{}': {}", user.getUsername(), e.getMessage(), e);
            throw e; // Rethrow the exception to propagate it upwards
        }
    }

    /**
     * Add a favorite city for the specified user.
     *
     * @param cityName - The name of the city to add
     * @param user     - The logged-in user
     * @return The newly saved FavoriteCity entity
     */
    public FavoriteCity addFavoriteCity(String cityName, User user) {
        validateCityName(cityName);
        validateUser(user);

        try {
            logger.debug("Adding favorite city '{}' for user '{}'", cityName, user.getUsername());

            // Check if the city is already a favorite for the user
            Optional<FavoriteCity> existingCity = favoriteCityRepository.findByNameAndUser(cityName.trim(), user);
            if (existingCity.isPresent()) {
                logger.warn("City '{}' is already a favorite for user '{}'", cityName, user.getUsername());
                throw new IllegalStateException("City is already marked as a favorite!");
            }

            // Save the new favorite city
            FavoriteCity favoriteCity = new FavoriteCity(cityName.trim(), user);
            return favoriteCityRepository.save(favoriteCity);
        } catch (Exception e) {
            logger.error("Error adding favorite city '{}': {}", cityName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Remove a favorite city for the specified user.
     *
     * @param cityName - The name of the city to remove
     * @param user     - The logged-in user
     */
    @Transactional
    public void removeFavoriteCity(String cityName, User user) {
        validateCityName(cityName);
        validateUser(user);

        try {
            logger.debug("Removing favorite city '{}' for user '{}'", cityName, user.getUsername());
            favoriteCityRepository.deleteByNameAndUser(cityName.trim(), user);
        } catch (Exception e) {
            logger.error("Error removing favorite city '{}' for user '{}': {}", cityName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Utility method to validate the city name.
     *
     * @param cityName - The city name to validate
     */
    private void validateCityName(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            logger.error("City name cannot be null or empty!");
            throw new IllegalArgumentException("City name must be provided!");
        }
    }

    /**
     * Utility method to validate the user.
     *
     * @param user - The user to validate
     */
    private void validateUser(User user) {
        if (user == null) {
            logger.error("User cannot be null!");
            throw new IllegalArgumentException("User must be provided!");
        }
    }
}