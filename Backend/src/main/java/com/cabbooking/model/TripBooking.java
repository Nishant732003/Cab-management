package com.cabbooking.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * TripBooking entity representing a cab trip booking in the system.
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TripBooking {

    /**
     * The unique identifier for the trip booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tripBookingId;

    /**
     * The customer who booked the trip. This establishes a Many-to-One
     * relationship with the Customer entity. A customer can have many trip
     * bookings.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    /**
     * The driver assigned to the trip. This is a Many-to-One relationship, as a
     * driver can handle multiple trips over time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    @JsonIgnore
    private Driver driver;

    /**
     * The cab used for the trip. Establishes a Many-to-One relationship with
     * the Cab entity. This will be integrated with the Cab Management module.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cab_id")
    @JsonIgnore
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
     * The current status of the trip (e.g., CONFIRMED, COMPLETED). Uses an Enum
     * for type-safety and clarity.
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

    /**
     * Customer rating for the trip (1-5 stars)
     */
    private Integer customerRating;

    /**
     * The type of car requested by the customer for the trip (e.g., "Sedan",
     * "SUV"). This is crucial for the scheduler to assign the correct type of
     * cab.
     */
    private String carType;

    /**
     * From Location's latitude
     */
    private Double fromLatitude;

    /**
     * From Location's longitude
     */
    private Double fromLongitude;

    // Constructors
    public TripBooking() {
    }

    public TripBooking(Integer tripBookingId, String fromLocation, String toLocation,
            LocalDateTime fromDateTime, LocalDateTime toDateTime,
            TripStatus status, float distanceInKm, float bill,
            Integer customerRating, String carType,
            Double fromLatitude, Double fromLongitude) {
        this.tripBookingId = tripBookingId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.status = status;
        this.distanceInKm = distanceInKm;
        this.bill = bill;
        this.customerRating = customerRating;
        this.carType = carType;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
    }

    // ======= Getters and Setters =======
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

    public Integer getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Integer customerRating) {
        this.customerRating = customerRating;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Double getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(Double fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public Double getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(Double fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

    @Override
    public String toString() {
        return "TripBooking{"
                + "tripBookingId=" + tripBookingId
                + ", fromLocation='" + fromLocation + '\''
                + ", toLocation='" + toLocation + '\''
                + ", fromDateTime=" + fromDateTime
                + ", toDateTime=" + toDateTime
                + ", status=" + status
                + ", distanceInKm=" + distanceInKm
                + ", bill=" + bill
                + ", customerRating=" + customerRating
                + ", carType='" + carType + '\''
                + ", fromLatitude=" + fromLatitude
                + ", fromLongitude=" + fromLongitude
                + '}';
    }
}
