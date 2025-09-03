package com.cabbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
 * DTO for updating a cab's details.
 * 
 * This class encapsulates the data required to update a cab's details, such as
 * its number plate, type, and per-kilometer rate.
 * 
 * Validation annotations ensure that the incoming data meets expected criteria
 * before being further processed in the service layer.
 */
public class CabUpdateRequest {

    /*
     * Unique number plate of the cab to be updated.
     * 
     * NotBlank annotation ensures that the number plate is not null or empty.
     */
    @NotBlank(message = "Number plate is required")
    private String numberPlate;

    /*
     * Type of the cab to be updated.
     * 
     * NotBlank annotation ensures that the car type is not null or empty.
     */
    @NotBlank(message = "Car type is required")
    private String carType;

    /*
     * Per kilometer rate of the cab to be updated.
     * 
     * NotNull annotation ensures that the per kilometer rate is not null.
     */
    @NotNull(message = "Per kilometer rate is required")
    private Float perKmRate;

    // ====== Getters and Setters for fields ======
    public String getNumberPlate() { return numberPlate; }
    public void setNumberPlate(String numberPlate) { this.numberPlate = numberPlate; }
    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }
    public Float getPerKmRate() { return perKmRate; }
    public void setPerKmRate(Float perKmRate) { this.perKmRate = perKmRate; }
}