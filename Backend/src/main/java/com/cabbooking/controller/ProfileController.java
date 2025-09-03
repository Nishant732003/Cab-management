package com.cabbooking.controller;

import com.cabbooking.dto.UserProfileUpdateRequest;
import com.cabbooking.model.AbstractUser;
import com.cabbooking.service.IProfileService;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user profile-related operations.
 * 
 * Main Responsibilities: 
 * - Provides endpoints for fetching and updating user profiles.
 * 
 * Security: 
 * - All endpoints are secured using method-level security.
 * - Only Authenticated users can access the profile.
 * - Users can only update their own profile. 
 * - Admins can update any user's profile.
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
     * This endpoint is accessible to any authenticated user.
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
    @PreAuthorize("isAuthenticated()") // Any logged-in user can view a profile
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
     * Endpoint for a user to update their own profile details.
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
    @PreAuthorize("principal == #username")
    public ResponseEntity<AbstractUser> updateUserProfile(
            @PathVariable String username,
            @RequestBody UserProfileUpdateRequest request) {

        logger.info("Received update-profile request for username: {}", username);

        AbstractUser updatedUser = profileService.updateUserProfile(username, request);
        logger.info("Updated user profile for username: {}", username);
        return ResponseEntity.ok(updatedUser);
    }
}
