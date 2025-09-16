package com.cabbooking.service;

import java.io.IOException;

/**
 * Service interface for user account deletion operations.
 */
public interface IUserDeletionService {

    /**
     * Deletes a user from the system, regardless of their type (Admin, Customer, or Driver).
     *
     * @param username The unique username of the user to be deleted.
     * @throws IllegalArgumentException if no user with the given username is found.
     */
    void deleteUser(String username) throws IOException;
}