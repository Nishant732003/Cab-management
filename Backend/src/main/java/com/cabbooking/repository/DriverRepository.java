package com.cabbooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Driver;

/**
 * Repository interface for Driver entities.
 *
 * Extends JpaRepository providing standard CRUD operations and pagination
 * support for Driver entities with Integer as the primary key type.
 *
 * Declares a custom query method to find Driver by username, which is important
 * for authentication and user management.
 */
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    /**
     * Finds a Driver entity based on the unique username. Used primarily during
     * authentication processes.
     *
     * @param username The unique username of the driver
     * @return Driver entity if found; otherwise null.
     */
    Driver findByUsername(String username);

    /**
     * Finds an Admin entity by its unique email. Used primarily during login to
     * retrieve the admin user record.
     *
     * @param username the unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Optional<Driver> findByEmail(String email);

    /**
     * Checks if an Driver with the given username already exists.
     *
     * @param username The username to check.
     * @return True if an driver with that username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a Driver with the given email address already exists.
     *
     * @param email The email to check.
     * @return True if a driver with that email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Deletes a Driver entity by its unique username. This method is used to
     * remove a driver from the system.
     *
     * @param username The unique username of the driver to delete.
     */
    void deleteByUsername(String username);
}
