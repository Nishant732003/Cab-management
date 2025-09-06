package com.cabbooking.dto;

import com.cabbooking.model.TripStatus;
import java.time.LocalDateTime;

public class TripHistoryResponse {

    private Integer tripBookingId;
    private String fromLocation;
    private String toLocation;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
    private TripStatus status;
    private float bill;
    private Integer customerRating;
    private String carType;
    private String customerFirstName;
    private String customerLastName;
    private String driverFirstName;
    private String driverLastName;

    public TripHistoryResponse(Integer tripBookingId, String fromLocation, String toLocation, 
                               LocalDateTime fromDateTime, LocalDateTime toDateTime, 
                               TripStatus status, float bill, Integer customerRating, 
                               String carType, String customerFirstName, String customerLastName, 
                               String driverFirstName, String driverLastName) {
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
    }

    // Getters and Setters
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
}