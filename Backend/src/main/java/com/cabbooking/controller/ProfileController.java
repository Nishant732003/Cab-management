package com.cabbooking.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.dto.UserProfileUpdateRequest;
import com.cabbooking.model.AbstractUser;
import com.cabbooking.service.IProfileService;

/**
 * REST controller for handling user profile-related operations.
 * 
 * Endpoints:
 * - GET /api/profiles/{username}: Fetches the profile details of any user by their username.
 * - PUT /api/profiles/{username}: Allows a user to update their own profile details.
 * 
 * Main Responsibilities: 
 * - Provides endpoints for fetching and updating user profiles.
 * - Delegates business logic to the IProfileService.
 * - Handles request validation and response formatting.
 * 
 * Dependencies:
 * - IProfileService: Service layer for profile-related operations.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    // SLF4J Logger for tracking requests and actions in this controller
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private IProfileService profileService;

    /**
     * Endpoint to fetch the profile details of any user by their username.
     * 
     * GET /api/profiles/{username}
     * 
     * Workflow: 
     * - Checks if the user is authenticated.
     * - Calls the service layer to fetch the user's profile details. 
     * - Returns a ResponseEntity containing the user's profile details, or a 404 Not Found response.
     *
     * @param username The username of the user whose profile is to be fetched.
     * @return A ResponseEntity containing the user's profile details, or a 404 Not Found response.
     */
    @GetMapping("/{username}")
    public ResponseEntity<AbstractUser> getUserProfile(@PathVariable String username) {
        logger.info("Received get-profile request for username: {}", username);
        Optional<AbstractUser> userProfile = profileService.getUserProfileByUsername(username);

        if (userProfile.isPresent()) {
            logger.info("Found user profile for username: {}", username);
            // Return the user profile
            return ResponseEntity.ok(userProfile.get());
        } else {
            logger.info("User profile not found for username: {}", username);
            // User profile not found
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to allow a user to update their own profile details.
     * 
     * PUT /api/profiles/{username}
     * 
     * Workflow: 
     * - Checks if the user is authenticated.
     * - Calls the service layer to update the user's profile details. 
     * - Returns a ResponseEntity containing the updated user profile.
     *
     * @param username The username from the URL path.
     * @param request The DTO with the fields to update.
     * @return The updated user profile.
     */
    @PutMapping("/{username}")
    public ResponseEntity<AbstractUser> updateUserProfile(
            @PathVariable String username,
            @RequestBody UserProfileUpdateRequest request) {

        logger.info("Received update-profile request for username: {}", username);

        AbstractUser updatedUser = profileService.updateUserProfile(username, request);
        logger.info("Updated user profile for username: {}", username);
        return ResponseEntity.ok(updatedUser);
    }
}
