package com.Likun.weatherdashboard.repository;

import com.Likun.weatherdashboard.model.FavoriteCity;
import com.Likun.weatherdashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCityRepository extends JpaRepository<FavoriteCity, Long> {
    /**
     * Find all favorite cities for a given user.
     */
    List<FavoriteCity> findByUser(User user);

    /**
     * Check if the favorite city already exists for the user.
     */
    Optional<FavoriteCity> findByNameAndUser(String cityName, User user);

    /**
     * Delete a favorite city by name and user.
     */
    void deleteByNameAndUser(String cityName, User user);
}