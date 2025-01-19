package com.Likun.weatherdashboard.config;

import com.Likun.weatherdashboard.filter.JwtFilter;
import com.Likun.weatherdashboard.service.CustomUserDetailsService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.Likun.weatherdashboard.repository.UserRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final AuthenticationProvider jwtAuthenticationProvider; // Custom AuthenticationProvider
    private final JwtFilter jwtFilter; // JWT Filter for token processing

    public SecurityConfig(AuthenticationProvider jwtAuthenticationProvider, JwtFilter jwtFilter) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/css/**", "/js/**",
                                "/navbar.html", "/login.html", "/register.html", "/dashboard.html")
                        .permitAll() // Permit static and public files
                        .requestMatchers("/api/login", "/api/register", "/api/validate-token").permitAll() // Unauthenticated endpoints
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless authentication
                )

                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .authenticationProvider(jwtAuthenticationProvider) // Register the custom AuthenticationProvider
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JwtFilter before UsernamePasswordAuthenticationFilter

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500",
                "https://my-weatherdashboard-app-0957e4d7c137.herokuapp.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Filter blockSessionCookies() {
        return (request, response, chain) -> {
            if (request instanceof HttpServletRequest httpRequest) {
                String sessionId = httpRequest.getRequestedSessionId();
                if (sessionId != null) {
                    System.out.println("Intercepted Session Cookie: " + sessionId);
                }
            }
            chain.doFilter(request, response);
        };
    }
}