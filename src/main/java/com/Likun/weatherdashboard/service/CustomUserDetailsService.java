package com.Likun.weatherdashboard.service;

import com.Likun.weatherdashboard.model.Authority;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;

import com.Likun.weatherdashboard.repository.AuthorityRepository;
import com.Likun.weatherdashboard.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Add this

    private final AuthorityRepository authorityRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository,
                                    AuthorityRepository authorityRepository,
                                    @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        logger.debug("Testing if CustomUserDetailsService is called for: {}", username);

        // Fetch user from the database
        com.Likun.weatherdashboard.model.User user =
                userRepository.findByUsername(username)
                        .orElseThrow(() -> {
                            logger.error("User '{}' not found in the database.", username);
                            return new UsernameNotFoundException("User not found: " + username);
                        });
        logger.info("Loaded user from database: {}", username);

        // Fetch the user's roles (authorities)
        List<String> roles = authorityRepository.findAuthoritiesByUsername(user.getUsername());
        if (roles.isEmpty()) {
            logger.error("No roles found for user '{}'.", username);
        } else {
            logger.info("User '{}' has roles: {}", username, roles);
        }

        // Map roles (authorities) to Spring Security's GrantedAuthority
        var grantedAuthorities = roles.stream()
                .map(role -> {
                    logger.debug("Mapping role '{}' to GrantedAuthority.", role);
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());

        // Log granted authorities
        logger.debug("Granted authorities for user '{}': {}", username, grantedAuthorities);

        return new User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true,
                true, grantedAuthorities);
    }

    public void registerUser(String username, String password, String role) {
        // Check if the username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken!");
        }

        // Encrypt the password before saving the user
        String encryptedPassword = passwordEncoder.encode(password);

        // Create a new user object
        com.Likun.weatherdashboard.model.User newUser = new com.Likun.weatherdashboard.model.User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setEnabled(true); // Enable by default (you can add email verification later if needed)

        // Save the user to the database
        userRepository.save(newUser);

        // Create an Authority entity for the role and save it
        Authority authority = new Authority();
        authority.setUsername(username);
        authority.setAuthority(role == null || role.isEmpty() ? "ROLE_USER" : role);

        // Check if authority saving works
        authority = authorityRepository.save(authority);
        if (authority != null && authority.getId() != null) {
            System.out.println("Role: " + authority.getAuthority() + " has been assigned to user: " + username);
        } else {
            System.out.println("Failed to assign role to user: " + username);
        }


        // For example, ROLE_USER or ROLE_ADMIN
        logger.info("New user '{}' successfully registered.", username);
    }
}