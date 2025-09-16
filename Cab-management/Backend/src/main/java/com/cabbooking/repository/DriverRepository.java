package com.cabbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Driver;

/**
 * DriverRepository interface for performing CRUD operations on Driver entities.
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
     * @param email The unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Driver findByEmail(String email);

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

    /**
     * Finds all Driver entities where the 'verified' status is false. This is
     * an efficient, database-level query.
     *
     * @return A list of unverified drivers.
     */
    List<Driver> findByVerifiedFalse();
}
