package com.cabbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object for sending a summary of user information to the client.
 * This is used by the admin dashboard to display lists of users without sending sensitive data like passwords.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    
    /**
     * The unique identifier for the user.
     * Renamed from 'id' to 'userId' to align with the frontend's data model.
     */
    private Integer userId; 
    
    /**
     * The user's unique username.
     */
    private String username;
    
    /**
     * The user's email address.
     */
    private String email;
    
    /**
     * The full name of the user.
     */
    private String name;
    
    /**
     * The user's mobile phone number.
     */
    private String mobileNumber; 
}

