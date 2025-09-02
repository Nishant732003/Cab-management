package com.cabbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object for sending a summary of user information to the client.
 * This is used by the admin dashboard to display lists of users without sending sensitive data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    // Constructor for Customer
    public UserSummaryDTO(Integer userId, String username, String email, String name, String mobileNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }
}

