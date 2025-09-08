package com.cabbooking.service;

import java.time.LocalDate;
import java.util.List;

import com.cabbooking.dto.TripHistoryResponse;
import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;

/**
 * Service interface defining the business logic for the Trip Booking module.
 */
public interface ITripBookingService {

    /**
     * Creates a new trip booking based on a customer's request.
     *
     * @param tripBookingRequest DTO containing the details of the trip to be
     * booked.
     * @return The newly created {@link TripBooking} entity.
     */
    TripBooking bookTrip(TripBookingRequest tripBookingRequest);

    /**
     * Updates the status of an existing trip.
     *
     * @param tripId The ID of the trip to update.
     * @param status The new status for the trip (e.g., "IN_PROGRESS",
     * "CANCELLED").
     * @return The updated {@link TripBooking} entity.
     */
    TripBooking updateTripStatus(Integer tripId, String status, String driverUsername);

    /**
     * Marks a trip as completed and calculates the final bill.
     *
     * @param tripId The ID of the trip to complete.
     * @return The completed {@link TripBooking} entity with the final bill
     * calculated.
     */
    TripBooking completeTrip(Integer tripId, String driverUsername);

    /**
     * Retrieves all trips for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A list of {@link TripBooking} entities representing the customer's trip history.
     */
    List<TripHistoryResponse> getAllTripsCustomer(Integer customerId);

    /**
     * Applies a customer's rating to a completed trip.
     *
     * @param tripId The ID of the trip to rate.
     * @param ratingRequest The DTO containing the rating value.
     * @param customerUsername The username of the customer making the request.
     * @return The updated TripBooking object.
     */
    TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest, String customerUsername);

    /**
     * Retrieves all trips taken by a specific driver.
     * 
     * @param driverId The ID of the driver.
     * @return A list of trips.
     */
    List<TripHistoryResponse> getTripsByDriver(Integer driverId);

    /**
     * Retrieves all trips that occurred on a specific date.
     * 
     * @param date The date to search for.
     * @return A list of trips.
     */
    List<TripHistoryResponse> getTripsByDate(LocalDate date);

    /*
     * Retrieves the most recent trip for a customer to view their bill.
     * 
     * @param customerId The ID of the customer.
     * @return The latest TripBooking object for the customer.
     */
    TripBooking getBill(int customerId);
}
