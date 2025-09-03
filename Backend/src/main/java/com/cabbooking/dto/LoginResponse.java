package com.cabbooking.dto;

public class LoginResponse {

    private String message;
    private Integer userId;
    private String userType;
    private String token;
    private boolean success;

    public LoginResponse(String message, Integer userId, String userType, String token, boolean success) {
        this.message = message;
        this.success = success;
        this.token = token;
        this.userId = userId;
        this.userType = userType;
    }

    // Getters and Setters

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