package com.cabbooking.dto;

/**
 * A Data Transfer Object for sending a summary of user information to the client.
 * This is used by the admin dashboard to display lists of users without sending sensitive data.
 */
public class UserSummaryDTO {
    
    // Common fields for all users
    private Integer userId; 
    private String username;
    private String email;
    private String name;
    private String mobileNumber; 
    
    // Driver-specific fields (will be null for customers)
    private Double rating;
    private String licenceNo;
    private Boolean verified;

    public UserSummaryDTO() {
        // Default constructor
    }

    // Constructor for Customer
    public UserSummaryDTO(Integer userId, String username, String email, String name, String mobileNumber, Double rating, String licenceNo, Boolean verified) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.rating = rating;
        this.licenceNo = licenceNo;
        this.verified = verified;
    }

    // Getters and Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}

