package com.Likun.weatherdashboard.controller;

import com.Likun.weatherdashboard.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getCurrentWeather(@RequestParam String city) {
        try {
            Map<String, Object> weatherData = weatherService.getCurrentWeather(city);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            // Always return JSON, even for errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "City not found or something went wrong.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<?> getWeatherForecast(@RequestParam String city) {
        try {
            var forecastData = weatherService.getWeatherForecast(city);
            return ResponseEntity.ok(forecastData);
        } catch (Exception e) {
            // Always return JSON, even for errors
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "City not found or something went wrong.");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}