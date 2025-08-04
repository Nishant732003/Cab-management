package com.cabbooking.dto;

/**
 * LoginRequest represents the data sent from the client to the server
 * when a user attempts to log in.
 *
 * It contains the essential credentials needed to authenticate:
 * - username: The user's login identifier (unique for each user).
 * - password: The user's secret password.
 *
 * This class is used to receive login input from REST API requests.
 */
public class LoginRequest {

    /**
     * The username provided by the user for login.
     */
    private String username;

    /**
     * The password provided by the user for authentication.
     */
    private String password;

    // ====== Getters and Setters ======

    /**
     * Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
