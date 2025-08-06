package com.cabbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfig is the Spring Security configuration class for the Cab Booking backend.
 * 
 * Responsibilities:
 * - Define password encoding mechanism for securely storing user passwords.
 * - Configure HTTP security rules for URL access permissions and authentication mechanisms.
 * - Expose publicly accessible endpoints (login, customer/driver/admin registrations, and H2 console).
 * - Disable CSRF protection only on allowed paths (especially H2 console) to facilitate development.
 * - Enable HTTP Basic Authentication for backend API testing and early development.
 *
 * Note: This configuration is currently set up for development convenience and must be tightened 
 *       for production deployment (e.g., enabling CSRF globally, switching to JWT instead of HTTP Basic Auth).
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates and exposes a PasswordEncoder bean using BCrypt hashing algorithm.
     * 
     * BCryptPasswordEncoder applies a strong hashing function with salting to protect passwords
     * against brute force and rainbow table attacks.
     * 
     * This bean is injected in user registration and authentication services where
     * passwords are encoded and verified.
     * 
     * @return PasswordEncoder instance using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines the security filter chain that applies Spring Security's HTTP protections and policies.
     * This method configures:
     * - CSRF disabling for specific URLs (like H2 console) to avoid blocking legitimate requests
     * - HTTP header settings to allow H2 console to be embedded in a browser frame
     * - Access rules (which endpoints are public & which require authentication)
     * - HTTP Basic Authentication for API clients like Postman during development
     * 
     * @param http the HttpSecurity object to configure
     * @return built SecurityFilterChain instance
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection only for H2 console URLs to allow UI operation
            // Global disablement for now to ease API testing, but NOT recommended for production
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/h2-console/**")
            ).disable())  

            // Allow H2 console UI to be displayed inside a frame (iframe) in browsers
            // Default Spring Security blocks this for security reasons
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) 

            // Define URL authorization rules
            .authorizeHttpRequests(authz -> authz
                // Make login and registration endpoints publicly accessible (no login required)
                .requestMatchers(
                    "/api/login",
                    "/api/customers/register",
                    "/api/drivers/register",
                    "/api/admins/register",
                    "/h2-console/**"
                ).permitAll()

                // Require authentication for all other requests/endpoints
                .anyRequest().authenticated()
            )

            // Set up HTTP Basic Authentication (username and password sent in HTTP headers).
            // Useful for simple testing but should be replaced with more secure protocols later.
            .httpBasic(Customizer.withDefaults());

        // Build and return the constructed filter chain
        return http.build();
    }
}
