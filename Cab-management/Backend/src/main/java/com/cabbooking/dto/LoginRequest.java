package com.cabbooking.dto;

/**
 * DTO for capturing login request data.
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

    /*
     * The email provided by the user for login.
     */
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
