package com.cabbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for requesting a password reset link.
 */
public class PasswordResetRequest {

    /*
     * The email address of the user requesting a password reset.
     * Required and should be a valid email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be a valid format")
    private String email;

    // ======= Getters and Setters =======
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
