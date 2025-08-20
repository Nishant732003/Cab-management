package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the data needed to register a new
 * Driver.
 *
 * This class is used to capture the registration details submitted by a driver
 * through the REST API or other client interfaces.
 *
 * Validation annotations are applied to enforce constraints on the input data,
 * helping to ensure data integrity before processing in service layers.
 */
public class DriverRegistrationRequest {

    /**
     * The username chosen by the driver.
     *
     * This field is mandatory and must not be blank. It is used as the driver's
     * unique login identifier.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password chosen by the driver.
     *
     * Must not be blank and should have a minimum length of 6 characters.
     * Passwords should be securely hashed when stored.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * The email address of the driver.
     *
     * Optional, but if provided, must be in a valid email format.
     */
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The address of the driver.
     *
     * Optional field containing contact or residential address.
     */
    private String address;

    /**
     * The driver's mobile phone number.
     *
     * Optional; may be used for contact or verification purposes.
     */
    private String mobileNumber;

    /**
     * The driver's license number.
     *
     * This is a mandatory field and cannot be blank. It is essential for the
     * verification and legal compliance of the driver.
     */
    @NotBlank(message = "License number is required")
    private String licenceNo;

    // ======= Getters and Setters =======
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }
}
