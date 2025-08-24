package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing the data required to register a new
 * Customer in the Cab Booking Platform.
 *
 * This DTO is used to transfer data from client requests (e.g., JSON payload in
 * REST API) into your backend application for processing customer
 * registrations.
 *
 * Validation annotations ensure that the incoming data meets expected criteria
 * before being further processed in the service layer.
 */
public class CustomerRegistrationRequest {

    /**
     * The unique username chosen by the customer
     *
     * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password chosen by the customer
     *
     * This field is mandatory and must have a minimum length of 6 characters.
     * Passwords will be hashed in the backend before being stored.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * The customer's email address
     */
    @NotBlank(message = "Password is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The customer's physical or mailing address.
     *
     * Optional field that can be used for contact or billing purposes.
     */
    private String address;

    /**
     * The customer's mobile phone number.
     *
     * Optional field, potentially useful for contact or multi-factor
     * authentication.
     */
    private String mobileNumber;

    // ====== Getters and Setters for fields ======
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
