package com.cabbooking.dto;

import java.time.LocalDateTime;

import com.cabbooking.model.TripStatus;

/*
 * DTO for representing trip history details.
 */
public class TripHistoryResponse {

    /*
     * The unique identifier for the trip booking.
     */
    private Integer tripBookingId;

    /*
     * The starting location for the trip.
     */
    private String fromLocation;

    /*
     * The destination for the trip.
     */
    private String toLocation;

    /*
     * The start date and time of the trip.
     */
    private LocalDateTime fromDateTime;

    /*
     * The end date and time of the trip.
     */
    private LocalDateTime toDateTime;

    /*
     * The current status of the trip (e.g., COMPLETED, CANCELLED).
     */
    private TripStatus status;

    /*
     * The total bill amount for the trip.
     */
    private float bill;

    /*
     * The rating given by the customer for the trip (1-5).
     */
    private Integer customerRating;

    /*
     * The car type used for the trip (e.g., Sedan, SUV).
     */
    private String carType;

    /*
     * Customer's first name
     */
    private String customerFirstName;

    /*
     * Customer's last name
     */
    private String customerLastName;

    /*
     * Driver's first name
     */
    private String driverFirstName;

    /*
     * Driver's last name
     */
    private String driverLastName;

    /*
     * Distance covered during the trip in kilometers
     */
    private Number distanceinKm;

    /*
     * Driver ID
     */
    private Number driverId;

    /*
     * Customer ID
     */
    private Number customerId;

    /*
     * Cab ID
     */
    private Number cabId;

    // ======= Constructor =======
    public TripHistoryResponse(Integer tripBookingId, String fromLocation, String toLocation,
            LocalDateTime fromDateTime, LocalDateTime toDateTime,
            TripStatus status, float bill, Integer customerRating,
            String carType, String customerFirstName, String customerLastName,
            String driverFirstName, String driverLastName, Number distanceinKm, Number driverId, Number customerId, Number cabId) {
        this.tripBookingId = tripBookingId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.status = status;
        this.bill = bill;
        this.customerRating = customerRating;
        this.carType = carType;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.distanceinKm = distanceinKm;
        this.driverId = driverId;
        this.customerId = customerId;
        this.cabId = cabId;
    }

    // ======= Getters and Setters =======
    public Integer getTripBookingId() {
        return tripBookingId;
    }

    public void setTripBookingId(Integer tripBookingId) {
        this.tripBookingId = tripBookingId;
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

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public Number getDistanceinKm() {
        return distanceinKm;
    }

    public void setDistanceinKm(Number distanceinKm) {
        this.distanceinKm = distanceinKm;
    }

    public Number getDriverId() {
        return driverId;
    }

    public void setDriverId(Number driverId) {
        this.driverId = driverId;
    }

    public Number getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Number getCabId() {
        return cabId;
    }

    public void setCabId(Number cabId) {
        this.cabId = cabId;
    }
}
