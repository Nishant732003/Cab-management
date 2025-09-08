package com.cabbooking.dto;

/**
 * DTO for capturing login response data.
 */
public class LoginResponse {

    /**
     * Message indicating the success or failure of the login attempt.
     */
    private String message;

    /**
     * ID of the logged-in user.
     */
    private Integer userId;

    /**
     * Type of the logged-in user (e.g., "admin", "customer", "driver").
     */
    private String userType;

    /**
     * JWT token for authentication.
     */
    private String token;

    /*
     * Indicates whether the login was successful.
     */
    private boolean success;

    public LoginResponse(String message, Integer userId, String userType, String token, boolean success) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.userId = userId;
        this.userType = userType;
    }

    // ======= Getters and Setters =======
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

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}