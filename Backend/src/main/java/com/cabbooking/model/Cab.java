package com.cabbooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a Cab vehicle in the system.
 * This is a placeholder entity that the TripBooking module depends on.
 * The Cab Management module will expand this with more details and business logic.
 *
 * An Admin will be responsible for adding and managing these Cab entities.
 */
@Entity
public class Cab {

    /**
     * The unique identifier for the cab.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cabId;

    /**
     * The type of the car (e.g., "Sedan", "SUV", "Hatchback").
     * This will be used for filtering and pricing.
     */
    private String carType;

    /**
     * The rate charged per kilometer for this cab.
     * This is crucial for calculating the final bill of a trip.
     */
    private Float perKmRate;

    // Getters and Setters
    public Integer getCabId() {
        return cabId;
    }

    public void setCabId(Integer cabId) {
        this.cabId = cabId;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Float getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(Float perKmRate) {
        this.perKmRate = perKmRate;
    }
}
