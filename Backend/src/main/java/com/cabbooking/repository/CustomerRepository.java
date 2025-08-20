package com.cabbooking.repository;

import com.cabbooking.model.Customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Customer entities.
 *
 * Extends JpaRepository to provide CRUD functionality and pagination support
 * for the Customer entity with Integer as the primary key type.
 *
 * Additional query methods specific to Customer entities can be declared here.
 *
 * Includes a method to find a Customer by username, which is essential for
 * login and user management features.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Finds a Customer by their unique username. Used for authentication and
     * data retrieval.
     *
     * @param username The unique username of the customer.
     * @return Customer entity matching the username, or null if none found.
     */
    Customer findByUsername(String username);

    /**
     * Finds an Admin entity by its unique email. Used primarily during login to
     * retrieve the admin user record.
     *
     * @param username the unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Checks if an Customer with the given username already exists.
     *
     * @param username The username to check.
     * @return True if an customer with that username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a Customer with the given email address already exists.
     *
     * @param email The email to check.
     * @return True if a customer with that email exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Deletes a Customer by their username. This method is used to remove a
     * customer from the database.
     *
     * @param username The unique username of the customer to delete.
     */
    void deleteByUsername(String username);
}
