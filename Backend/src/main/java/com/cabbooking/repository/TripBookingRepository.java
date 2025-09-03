package com.cabbooking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cabbooking.model.TripBooking;

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
    // Correct way to query by the ID of the nested Customer object
    List<TripBooking> findByCustomer_Id(Integer customerId);

    /**
     * Finds all trips associated with a specific driver.
     * @param driverId The ID of the driver.
     * @return A list of trips for that driver.
     */
    List<TripBooking> findByDriver_Id(Integer driverId);

    /**
     * Finds all trips that started within a given date and time range.
     * @param startOfDay The beginning of the day.
     * @param endOfDay The end of the day.
     * @return A list of trips that started on that day.
     */
    List<TripBooking> findByFromDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
