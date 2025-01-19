package com.Likun.weatherdashboard.filter;

import com.Likun.weatherdashboard.service.CustomUserDetailsService;
import com.Likun.weatherdashboard.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private Environment environment;

    private List<String> excludedUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Lazy-load excluded URLs (includes static files and other public endpoints)
        if (excludedUrls == null) {
            excludedUrls = List.of(environment.getProperty("security.exclude-urls", "").split(",")).stream()
                    .map(String::trim)
                    .toList();
        }

        String requestUri = request.getRequestURI();
        logger.debug("Processing request URI '{}'", requestUri);

        if (isExcluded(requestUri)) {
            logger.debug("Skipping JWT filter for excluded request '{}'", requestUri);
            SecurityContextHolder.clearContext(); // Reset security context for excluded URLs
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header on URI '{}'", requestUri);
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response); // Continue without authentication
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();

            var authorities = jwtUtil.extractAuthorities(claims);

            // Set valid authentication token in the SecurityContext
            var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("SecurityContext after setting token: {}", SecurityContextHolder.getContext().getAuthentication());
            logger.debug("Token validated successfully and SecurityContext set for user '{}'", username);

        } catch (Exception e) {
            logger.error("Invalid token for request '{}': {}", requestUri, e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(String requestUri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return excludedUrls.stream().anyMatch(excluded -> pathMatcher.match(excluded, requestUri));
    }
}