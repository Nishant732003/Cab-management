package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for registering a new admin user.
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
     * First name of the admin user.
     * 
     * Must be provided (not blank).
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * First name of the admin user.
     * 
     * Must be provided (not blank).
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

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
     * Must be provided (not blank) and follow a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Address of the admin user.
     * 
     * Must be provided (not blank).
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * Mobile phone number of the admin.
     * 
     * Must be provided (not blank).
     */
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    // ===== Getters and Setters =====
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
