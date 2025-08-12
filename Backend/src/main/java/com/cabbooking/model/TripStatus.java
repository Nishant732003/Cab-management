package com.cabbooking.model;

/**
 * Defines the possible states of a TripBooking.
 * Using an enum for trip status provides type safety and makes the code more readable
 * and maintainable compared to using simple strings or integers.
 */
public enum TripStatus {
    /**
     * The trip has been successfully booked by a customer and is waiting for a driver to start.
     */
    CONFIRMED,

    /**
     * The driver has started the trip, and it is currently underway.
     */
    IN_PROGRESS,

    /**
     * The trip has been successfully completed by the driver.
     */
    COMPLETED,

    /**
     * The trip has been cancelled by either the customer or the driver.
     */
    CANCELLED
}
