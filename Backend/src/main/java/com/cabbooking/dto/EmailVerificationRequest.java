package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for sending an email address to the server, typically for verification
 * purposes.
 */
public class EmailVerificationRequest {

    /**
     * The email address to be verified.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    // ======= Getters and Setters =======
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
