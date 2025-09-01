package com.cabbooking.service;

import com.cabbooking.dto.RatingRequest;
import com.cabbooking.dto.TripBookingRequest;
import com.cabbooking.model.TripBooking;

import java.util.List;

/**
 * Service interface defining the business logic for the Trip Booking module.
 */
public interface ITripBookingService {

    /**
     * Creates a new trip booking based on a customer's request.
     *
     * @param tripBookingRequest DTO containing the details of the trip to be booked.
     * @return The newly created {@link TripBooking} entity.
     */
    TripBooking bookTrip(TripBookingRequest tripBookingRequest);

    /**
     * Updates the status of an existing trip.
     *
     * @param tripId The ID of the trip to update.
     * @param status The new status for the trip (e.g., "IN_PROGRESS", "CANCELLED").
     * @param driverUsername The username of the driver making the request.
     * @return The updated {@link TripBooking} entity.
     */
    TripBooking updateTripStatus(Integer tripId, String status, String driverUsername);

    /**
     * Marks a trip as completed and calculates the final bill.
     *
     * @param tripId The ID of the trip to complete.
     * @param driverUsername The username of the driver making the request.
     * @return The completed {@link TripBooking} entity with the final bill calculated.
     */
    TripBooking completeTrip(Integer tripId, String driverUsername);

    /**
     * Retrieves all trips for a specific customer.
     *
     * @param customerId The ID of the customer.
     * @return A list of {@link TripBooking} entities representing the customer's trip history.
     */
    List<TripBooking> getAllTripsCustomer(Integer customerId);

    /**
     * Applies a customer's rating to a completed trip.
     *
     * @param tripId The ID of the trip to rate.
     * @param ratingRequest The DTO containing the rating value.
     * @param customerUsername The username of the customer making the request.
     * @return The updated TripBooking object.
     */
    TripBooking rateTrip(Integer tripId, RatingRequest ratingRequest, String customerUsername);
}
