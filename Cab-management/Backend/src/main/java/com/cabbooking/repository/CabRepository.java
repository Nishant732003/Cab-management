package com.cabbooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.Cab;

/**
 * CabRepository interface for performing CRUD operations on Cab entities.
 */
public interface CabRepository extends JpaRepository<Cab, Integer> {

    /**
     * Finds all cabs that match a specific car type.
     *
     * This custom query is used to filter the fleet of cabs, for example, when
     * an admin wants to view all SUVs or a customer requests a specific type of
     * car.
     *
     * @param carType The car type to search for (e.g., "Sedan", "SUV").
     * @return A list of {@link Cab} entities that match the specified car type.
     */
    List<Cab> findByCarType(String carType);

    /**
     * Checks if a Cab with the given number plate already exists.
     *
     * @param numberPlate The number plate to check.
     * @return True if a cab with that number plate exists, false otherwise.
     */
    boolean existsByNumberPlate(String numberPlate);
}
