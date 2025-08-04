package com.cabbooking.dto;

/**
 * LoginResponse represents the data sent from the backend to the client
 * after a successful (or failed) login attempt.
 *
 * It provides:
 * - message: Success or failure message related to login.
 * - userId: The ID of the authenticated user (Admin, Customer, or Driver).
 * - userType: The type/role of the user (e.g., "Admin", "Customer", "Driver").
 *
 * This class is typically serialized to JSON and returned by login REST API.
 */
public class LoginResponse {

    /**
     * Response message indicating login success or failure details.
     */
    private String message;

    /**
     * Unique identifier of the authenticated user.
     */
    private Integer userId;

    /**
     * The role/type of the authenticated user (Admin, Customer, Driver).
     */
    private String userType;

    /**
     * Constructs a LoginResponse with all fields.
     * 
     * @param message Informational message about login result
     * @param userId  The authenticated user's ID
     * @param userType Role or user type as string
     */
    public LoginResponse(String message, Integer userId, String userType) {
        this.message = message;
        this.userId = userId;
        this.userType = userType;
    }

    // ===== Getters and Setters =====

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
}
