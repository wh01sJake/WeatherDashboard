package com.Likun.weatherdashboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://127.0.0.1:5500" , "https://my-weatherdashboard-app-0957e4d7c137.herokuapp.com","http://localhost:5500") // Removed trailing slash
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Include OPTIONS for preflight requests
                .allowedHeaders("Authorization", "Content-Type", "Accept") // Allow specific headers
                .exposedHeaders("Authorization") // Expose Authorization for client access
                .allowCredentials(true); // Allow cookies/credentials if required
    }
}