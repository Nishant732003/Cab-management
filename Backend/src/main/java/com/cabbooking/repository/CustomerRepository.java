package com.cabbooking.repository;

import com.cabbooking.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Customer entities.
 * 
 * Extends JpaRepository to provide CRUD functionality and pagination support
 * for the Customer entity with Integer as the primary key type.
 * 
 * Additional query methods specific to Customer entities can be declared here.
 * 
 * Includes a method to find a Customer by username, which is essential for login
 * and user management features.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    /**
     * Finds a Customer by their unique username.
     * Used for authentication and data retrieval.
     *
     * @param username The unique username of the customer.
     * @return Customer entity matching the username, or null if none found.
     */
    Customer findByUsername(String username);
}
