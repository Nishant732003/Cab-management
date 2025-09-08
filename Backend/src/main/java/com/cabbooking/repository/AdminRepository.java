package com.cabbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Admin;

/**
 * AdminRepository interface for performing CRUD operations on Admin entities.
 */
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    /**
     * Finds an Admin entity by its unique username. Used primarily during login
     * to retrieve the admin user record.
     *
     * @param username the unique username of the admin
     * @return Admin entity matching the username, or null if not found
     */
    Admin findByUsername(String username);

    /**
     * Finds an Admin entity by its unique email. Used primarily during login to
     * retrieve the admin user record.
     *
     * @param email The unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Admin findByEmail(String email);

    /**
     * Checks if an Admin with the given username already exists.
     *
     * @param username The username to check.
     * @return True if an admin with that username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an Admin with the given email address already exists.
     *
     * @param email The email to check.
     * @return True if an admin with that email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Deletes an Admin entity by its unique username. This method is used to
     * remove an admin from the system.
     *
     * @param username the unique username of the admin to delete
     */
    void deleteByUsername(String username);

    /**
     * Finds all Admin entities where the 'verified' status is false. This is an
     * efficient, database-level query.
     *
     * @return A list of unverified admins.
     */
    List<Admin> findByVerifiedFalse();
}
