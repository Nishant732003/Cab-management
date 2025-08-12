package com.cabbooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a single trip booking record in the system.
 * This entity is the core of the booking module, linking a customer to a driver and a cab for a specific journey.
 *
 * Workflow:
 * 1. A Customer initiates a request, which creates a TripBooking record with a 'CONFIRMED' status.
 * 2. A Driver is assigned to the trip.
 * 3. The trip status is updated as it progresses (e.g., 'IN_PROGRESS', 'COMPLETED').
 * 4. Upon completion, the final bill is calculated and stored.
 */
@Entity
public class TripBooking {

    /**
     * The unique identifier for the trip booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripBookingId;

    /**
     * The customer who booked the trip.
     * This establishes a Many-to-One relationship with the Customer entity.
     * A customer can have many trip bookings.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * The driver assigned to the trip.
     * This is a Many-to-One relationship, as a driver can handle multiple trips over time.
     */
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
    
    /**
     * The cab used for the trip.
     * Establishes a Many-to-One relationship with the Cab entity.
     * This will be integrated with the Cab Management module.
     */
    @ManyToOne
    @JoinColumn(name = "cab_id")
    private Cab cab;

    /**
     * The starting location of the trip.
     */
    private String fromLocation;

    /**
     * The destination of the trip.
     */
    private String toLocation;

    /**
     * The date and time when the trip was booked or started.
     */
    private LocalDateTime fromDateTime;

    /**
     * The date and time when the trip was completed.
     */
    private LocalDateTime toDateTime;

    /**
     * The current status of the trip (e.g., CONFIRMED, COMPLETED).
     * Uses an Enum for type-safety and clarity.
     */
    @Enumerated(EnumType.STRING)
    private TripStatus status;

    /**
     * The total distance of the trip in kilometers.
     */
    private float distanceInKm;

    /**
     * The final calculated bill for the trip.
     */
    private float bill;

    // Getters and Setters

    public Integer getTripBookingId() {
        return tripBookingId;
    }

    public void setTripBookingId(Integer tripBookingId) {
        this.tripBookingId = tripBookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
    
    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
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

    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(LocalDateTime toDateTime) {
        this.toDateTime = toDateTime;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public float getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(float distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public float getBill() {
        return bill;
    }

    public void setBill(float bill) {
        this.bill = bill;
    }
}
