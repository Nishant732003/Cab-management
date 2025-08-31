package com.cabbooking.dto;

/**
 * DTO representing login response including JWT token.
 */
public class LoginResponse {

    private String message;
    private Integer userId;
    private String userType;
    private String token;  // New field to carry JWT token

    public LoginResponse(String message, Integer userId, String userType, String token) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
        this.token = token;
    }

    // Getters and setters...

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
