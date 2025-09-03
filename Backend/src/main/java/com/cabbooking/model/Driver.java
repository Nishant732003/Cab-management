package com.cabbooking.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

/**
 * Represents a driver user in the cab booking platform.
 *
 * Main Responsibilities: - Extends AbstractUser to inherit common user
 * properties (username, password, etc.). - Stores driver-specific information
 * such as license number and rating. - Tracks the driver's verification and
 * availability status.
 *
 * Workflow: - A new Driver is created with a 'verified' status of false upon
 * registration. - An Admin must verify the driver to make them eligible for
 * trips. - The 'isAvailable' flag is used by the booking service to assign
 * trips.
 */
@Entity
public class Driver extends AbstractUser {

    /**
     * The driver's official license number. Essential for verification and
     * legal compliance.
     */
    private String licenceNo;

    /**
     * The driver's average rating, calculated from customer feedback. Used by
     * the system to prioritize higher-quality drivers for bookings.
     */
    private Float rating;

    /**
     * Indicates if the driver's account has been approved by an admin. Defaults
     * to false upon registration.
     */
    private Boolean verified = false;

    /**
     * Tracks the current availability of the driver for new trips. Defaults to
     * true for new, verified drivers.
     */
    private Boolean isAvailable = true;

    /**
     * The total number of ratings the driver has received. Used to accurately
     * calculate the new average rating.
     */
    private Integer totalRatings = 0;

    /**
     * URL to the driver's profile photo. Optional field to enhance user
     * experience in the app.
     */
    private String profilePhotoUrl;

    /*
     * Relationship to the Cab entity representing the driver's vehicle.
     * A driver can only drive one cab at a time.
     */
    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cab cab;

    /*
     * The driver's current location, represented as latitude
     */
    private Double latitude;

    /*
     * The driver's current location, represented as longitude
     */
    private Double longitude;

    // ===== Getters and Setters =====
    /**
     * Sets the availability status of the driver.
     *
     * @param isAvailable The new availability status.
     */
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    /**
     * Gets the current availability status of the driver.
     *
     * @return True if the driver is available, false otherwise.
     */
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    /**
     * Gets the driver's license number.
     *
     * @return The license number string.
     */
    public String getLicenceNo() {
        return licenceNo;
    }

    /**
     * Sets the driver's license number.
     *
     * @param licenceNo The license number to set.
     */
    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    /**
     * Gets the driver's current average rating.
     *
     * @return The rating as a Float.
     */
    public Float getRating() {
        return rating;
    }

    /**
     * Sets the driver's average rating.
     *
     * @param rating The new average rating to set.
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     * Gets the verification status of the driver.
     *
     * @return True if the driver is verified, false otherwise.
     */
    public Boolean getVerified() {
        return verified;
    }

    /**
     * Sets the verification status of the driver.
     *
     * @param verified The verification status to set.
     */
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    /**
     * Gets the total number of ratings received by the driver.
     *
     * @return The total number of ratings.
     */
    public Integer getTotalRatings() {
        return totalRatings;
    }

    /**
     * Sets the total number of ratings for the driver.
     *
     * @param totalRatings The new total ratings count.
     */
    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    /**
     * Gets the URL of the driver's profile photo.
     *
     * @return The profile photo URL as a String.
     */
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    /**
     * Sets the URL of the driver's profile photo.
     *
     * @param profilePhotoUrl The new profile photo URL to set.
     */
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
