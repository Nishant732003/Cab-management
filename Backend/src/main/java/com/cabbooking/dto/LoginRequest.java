package com.cabbooking.dto;

/**
 * LoginRequest represents the data sent from the client to the server when a
 * user attempts to log in.
 *
 * It contains the essential credentials needed to authenticate: - username: The
 * user's login identifier (unique for each user). - password: The user's secret
 * password.
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

    // ======= Getters and Setters =======
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
