package com.cabbooking.repository;

import com.cabbooking.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
