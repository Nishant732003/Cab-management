package com.cabbooking.repository;

import com.cabbooking.model.TripBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Spring Data JPA repository for {@link TripBooking} entities.
 * This interface provides standard CRUD (Create, Read, Update, Delete) operations
 * for the TripBooking table out of the box.
 *
 * It is used by the service layer to interact with the database for all trip-related data.
 */
public interface TripBookingRepository extends JpaRepository<TripBooking, Integer> {

    /**
     * Finds all trip bookings associated with a specific customer.
     * This is useful for fetching a customer's trip history.
     *
     * @param customerId The ID of the customer whose trips are to be retrieved.
     * @return A list of {@link TripBooking} entities.
     */
    List<TripBooking> findByCustomerId(Integer customerId);
}
