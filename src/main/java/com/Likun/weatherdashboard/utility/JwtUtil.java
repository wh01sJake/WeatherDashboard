package com.Likun.weatherdashboard.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class JwtUtil {

    private static final Logger logger = getLogger(JwtUtil.class);

    // Secret key for signing the token - store securely in prod
    private final String secret = "YourSuperSecretKeyForJwtSigningThatShouldBeVeryLongAndSecure";
    private final Key secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // Static secret key-based signing

    // Expiration time in milliseconds (e.g., 1 hour = 3600000)
    private final long expirationTime = 3600000;

    /**
     * Generates a JWT token for the provided username and roles.
     *
     * @param username the user's username
     * @param roles    the user's roles or authorities
     * @return the generated JWT token
     */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username) // Subject (e.g., username)
                .claim("roles", roles) // Add roles as token claims
                .setIssuedAt(new Date()) // Issued at current timestamp
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Expiration
                .signWith(secretKey) // Sign using HS256 and secret key
                .compact();

    }


    /**
     * Validates a JWT token and extracts claims.
     *
     * @param token the token to validate
     * @return Claims extracted from the token
     * @throws JwtException if the token is invalid or expired
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody(); // Extract claims
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
            throw e; // Let caller handle expired token
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            throw new JwtException("Invalid or expired token."); // General exception
        }
    }

    /**
     * Extract authorities (roles) from the token claims.
     *
     * @param claims JWT claims containing user data.
     * @return A list of GrantedAuthorities.
     */
    public List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<String> roles = claims.get("roles", List.class); // Expect roles to be a List<String>
        if (roles == null || roles.isEmpty()) {
            logger.warn("No roles found in token claims.");
            return List.of(); // Return empty authorities
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}