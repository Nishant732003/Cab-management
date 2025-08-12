package com.cabbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for handling incoming trip booking requests from a customer.
 * This class encapsulates the data required to create a new {@link com.cabbooking.model.TripBooking}.
 *
 * Using a DTO is a best practice as it decouples the API layer from the internal data model,
 * providing flexibility and security. It also allows for clear validation of incoming data.
 */
public class TripBookingRequest {

    /**
     * The ID of the customer who is booking the trip.
     */
    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    /**
     * The starting location for the trip.
     */
    @NotBlank(message = "From location is required")
    private String fromLocation;

    /**
     * The destination for the trip.
     */
    @NotBlank(message = "To location is required")
    private String toLocation;
    
    /**
     * The estimated distance of the trip in kilometers.
     * This will be used for initial fare estimation and final billing.
     */
    @NotNull(message = "Distance is required")
    private float distanceInKm;

    // Getters and Setters

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }
    
    public float getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(float distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
}
