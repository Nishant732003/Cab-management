package com.cabbooking.repository;

import com.cabbooking.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * Finds a Driver entity based on the unique username.
     * Used primarily during authentication processes.
     *
     * @param username The unique username of the driver
     * @return Driver entity if found; otherwise null.
     */
    Driver findByUsername(String username);
}
