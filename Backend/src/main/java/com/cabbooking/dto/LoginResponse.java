package com.cabbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login responses.
 * Contains user info, a JWT token, and a success flag.
 */
@Data // Generates all getters, setters, toString(), etc. automatically
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields (message, userId, userType, token, success)
public class LoginResponse {

    private String message;
    private Integer userId;
    private String userType;
    private String token;
    private boolean success;

}