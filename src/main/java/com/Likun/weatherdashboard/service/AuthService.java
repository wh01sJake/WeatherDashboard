package com.Likun.weatherdashboard.service;

import com.Likun.weatherdashboard.repository.AuthorityRepository;
import com.Likun.weatherdashboard.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Generates a JWT token for a user with their roles.
     *
     * @param username the user's username
     * @return the generated token
     */
    public String login(String username) {
        // Fetch user roles from the database
        List<String> roles = authorityRepository.findAuthoritiesByUsername(username);

        // Generate the JWT token with roles
        return jwtUtil.generateToken(username, roles);
        
    }
}