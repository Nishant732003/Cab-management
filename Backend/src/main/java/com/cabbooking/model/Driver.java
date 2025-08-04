package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Driver Entity:
 * 
 * Represents a driver user in the cab booking platform. Extends AbstractUser
 * so inherits common user properties like username, password, address, etc.
 * 
 * Additional fields specific to drivers:
 * - licenceNo: The driver's license number.
 * - rating: The driver's rating based on customer feedback.
 * 
 * This class is a JPA entity and maps to a DRIVER table in the database.
 * It is used in driver management, authentication, and trip assignments.
 */
@Entity
public class Driver extends AbstractUser {

    /**
     * The license number of the driver.
     * Used for identity verification and legal compliance.
     */
    private String licenceNo;

    /**
     * Driver's rating (e.g., from 0.0 to 5.0).
     * Used to assess driver quality and help customers choose drivers.
     */
    private Float rating;

    // ===== Getters and Setters =====

    /**
     * Gets the driver's license number.
     */
    public String getLicenceNo() {
        return licenceNo;
    }

    /**
     * Sets the driver's license number.
     */
    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    /**
     * Gets the current rating of the driver.
     */
    public Float getRating() {
        return rating;
    }

    /**
     * Sets the current rating of the driver.
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }
}
