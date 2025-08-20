package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the input data required to register a
 * new Admin user in the Cab Booking Platform.
 *
 * This class is used to receive admin registration details from client requests
 * (e.g., REST API JSON payload).
 *
 * Validation annotations ensure the data integrity at the input level by
 * enforcing mandatory fields and format constraints.
 */
public class AdminRegistrationRequest {

    /**
     * Username for the new admin account.
     *
     * Must be provided (not blank) and unique in the system.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Plain text password entered by the admin user.
     *
     * Must be provided (not blank) and have at least 6 characters. The password
     * will be hashed before storage.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * Email address of the admin.
     *
     * Optional, but if provided, must be a valid email format. Used for
     * communication or account recovery potentially.
     */
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Address of the admin user.
     *
     * Optional field to store physical or mailing address.
     */
    private String address;

    /**
     * Mobile phone number of the admin.
     *
     * Optional but can be used for contact or multi-factor authentication.
     */
    private String mobileNumber;

    // ===== Getters and Setters =====
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
}
