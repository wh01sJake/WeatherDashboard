package com.Likun.weatherdashboard.utility;
import com.Likun.weatherdashboard.utility.JwtUtil;

import java.util.List;

public class JwtTokenGenerator {
    public static void main(String[] args) {
        // Create an instance of JwtUtil
        JwtUtil jwtUtil = new JwtUtil();

        // Generate a token for testing
        String token = jwtUtil.generateToken("testUser", List.of("ROLE_ADMIN"));

        // Print the generated token to the console
        System.out.println("Generated Token: " + token);
    }
}
