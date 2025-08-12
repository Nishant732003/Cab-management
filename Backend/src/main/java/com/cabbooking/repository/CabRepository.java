package com.cabbooking.repository;

import com.cabbooking.model.Cab;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Cab} entities.
 * This is a placeholder repository. The Cab Management module, which is being
 * developed separately, will use this to manage cab data.
 *
 * It provides the basic database operations needed for the TripBooking module
 * to function, such as retrieving cab details for bill calculation.
 */
public interface CabRepository extends JpaRepository<Cab, Integer> {
}
