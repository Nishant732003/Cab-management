package com.cabbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Admin;

import java.util.Optional;

/**
 * Repository interface for Admin entities.
 *
 * Extends JpaRepository to provide basic CRUD operations and pagination
 * support for Admin entities with Integer as the primary key type.
 *
 * Additional custom database query methods related to Admin can be
 * declared here.
 *
 * This repository permits searching Admin entities by username, which
 * is essential for authentication and other admin-specific queries.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
    /**
     * Finds an Admin entity by its unique username.
     * Used primarily during login to retrieve the admin user record.
     *
     * @param username the unique username of the admin
     * @return Admin entity matching the username, or null if not found
     */
    Admin findByUsername(String username);

    /**
     * Finds an Admin entity by its unique email.
     * Used primarily during login to retrieve the admin user record.
     *
     * @param username the unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Checks if an Admin with the given username already exists.
     * @param username The username to check.
     * @return True if an admin with that username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an Admin with the given email address already exists.
     * @param email The email to check.
     * @return True if an admin with that email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Deletes an Admin entity by its unique username.
     * This method is used to remove an admin from the system.
     *
     * @param username the unique username of the admin to delete
     */
    void deleteByUsername(String username);
}
