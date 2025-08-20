package com.cabbooking.dto;

/**
 * DTO representing login response including JWT token.
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

    public LoginResponse(String message, Integer userId, String userType, String token) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
        this.token = token;
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
}
