package com.cabbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Cab;

/**
 * Spring Data JPA repository for {@link Cab} entities.
 *
 * Main Responsibilities:
 * - Provides standard CRUD (Create, Read, Update, Delete) operations for the Cab table.
 * - Enables the service layer to interact with the database for all cab-related data.
 *
 * Workflow:
 * - This interface is used by services like `CabServiceImpl` and `TripBookingServiceImpl`
 * to manage the fleet of cabs and find available vehicles for trips.
 */
public interface CabRepository extends JpaRepository<Cab, Integer> {

    /**
     * Finds all cabs that match a specific car type.
     *
     * This custom query is used to filter the fleet of cabs, for example,
     * when an admin wants to view all SUVs or a customer requests a specific type of car.
     *
     * @param carType The car type to search for (e.g., "Sedan", "SUV").
     * @return A list of {@link Cab} entities that match the specified car type.
     */
    List<Cab> findByCarType(String carType);
}