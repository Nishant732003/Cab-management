package com.cabbooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Customer;

/**
 * CustomerRepository interface for performing CRUD operations on Customer
 * entities.
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
     * @param email The unique email of the admin
     * @return Admin entity matching the email, or null if not found
     */
    Customer findByEmail(String email);

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
