package com.cabbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for submitting a new password along with the reset token.
 */
public class PasswordResetSubmission {

    /*
     * The reset token provided by the user
     */
    @NotBlank(message = "Token is required")
    private String token;

    /*
     * The new password provided by the user
     */
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;

    // ======= Getters and Setters =======
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
