package com.cabbooking.service;

import com.cabbooking.dto.UserProfileUpdateRequest;
import com.cabbooking.model.AbstractUser;
import java.util.Optional;

/**
 * Service interface for managing user profile updates.
 */
public interface IProfileService {

    /**
     * Partially updates a user's profile with the provided data using their username.
     *
     * @param username The unique username of the user to update.
     * @param request The DTO containing the fields to update.
     * @return The updated user object.
     */
    AbstractUser updateUserProfile(String username, UserProfileUpdateRequest request);

    /**
     * Retrieves a user's profile details by their unique username.
     * @param username The username of the user to find.
     * @return An Optional containing the user if found, otherwise empty.
     */
    Optional<AbstractUser> getUserProfileByUsername(String username);
}