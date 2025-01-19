package com.Likun.weatherdashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String apiKey = "YOU API KEY HERE";
    private final String baseUrl = "https://api.openweathermap.org/data/2.5";

    public Map<String, Object> getCurrentWeather(String city) throws Exception {
        try {
            String url = String.format("%s/weather?q=%s&APPID=%s", baseUrl, city, apiKey);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new Exception("City not found.");
            }

            return processWeatherResponse(response);
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching weather data: " + e.getStatusCode());
        }
    }

    public List<Map<String, Object>> getWeatherForecast(String city) throws Exception {
        try {
            String url = String.format("%s/forecast?q=%s&APPID=%s", baseUrl, city, apiKey);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.isEmpty()) {
                throw new Exception("City not found.");
            }

            return processForecastResponse(response);
        } catch (HttpClientErrorException e) {
            throw new Exception("Error fetching weather forecast: " + e.getStatusCode());
        }
    }

    private Map<String, Object> processWeatherResponse(Map<String, Object> response) {
        Map<String, Object> processedData = new HashMap<>();
        processedData.put("city", response.get("name"));
        processedData.put("country", ((Map<?, ?>) response.get("sys")).get("country"));
        Map<?, ?> main = (Map<?, ?>) response.get("main");
        processedData.put("temperature", Math.round((Double) main.get("temp") - 273.15));
        processedData.put("humidity", main.get("humidity"));
        processedData.put("weather", ((Map<?, ?>) ((List<?>) response.get("weather")).get(0)).get("description"));
        processedData.put("icon", ((Map<?, ?>) ((List<?>) response.get("weather")).get(0)).get("icon"));
        return processedData;
    }

    private List<Map<String, Object>> processForecastResponse(Map<String, Object> response) {
        // Process forecast data for the 5-day view
        List<Map<String, Object>> processedForecast = new java.util.ArrayList<>();
        for (Map<?, ?> item : (List<Map<?, ?>>) response.get("list")) {
            String dateTime = (String) item.get("dt_txt");
            if (dateTime.contains("12:00:00")) {
                Map<String, Object> forecastData = new HashMap<>();
                forecastData.put("date", dateTime);
                Map<?, ?> main = (Map<?, ?>) item.get("main");
                forecastData.put("temperature", Math.round((Double) main.get("temp") - 273.15));
                forecastData.put("weather", ((Map<?, ?>) ((List<?>) item.get("weather")).get(0)).get("description"));
                forecastData.put("icon", ((Map<?, ?>) ((List<?>) item.get("weather")).get(0)).get("icon"));
                processedForecast.add(forecastData);
            }
        }
        return processedForecast;
    }
}