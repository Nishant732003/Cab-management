package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for registering a new Admin.
 * It includes all the necessary details and validation for creating an admin account.
 */
public class AdminRegistrationRequest {


      /**
     * The customer's first name.
     * * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * The customer's last name.
     * * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;


    /**
     * The unique username for the admin.
     * This field is mandatory.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password for the admin account.
     * Must be at least 6 characters long.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * The admin's email address.
     * Must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The admin's physical address.
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * The admin's mobile phone number.
     */
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;
  
    // --- Getters and Setters ---


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