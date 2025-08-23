package com.cabbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PasswordResetToken {

    /*
     * Represents a token used for password reset requests.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The unique token associated with the password reset request.
     * It should be stored in a secure manner.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /*
     * The expiration date and time for the password reset token.
     * It should be set to a specific date and time in the future.
     * This will be used to determine if the token has expired.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /*
     * The email address associated with the password reset request.
     * It should be stored in a secure manner.
     * This will be used to identify the user associated with the password reset request.
     * This will be used to determine if the token has expired.
     */
    @Column(nullable = false)
    private String userEmail;

    // Constructors
    public PasswordResetToken() {}

    public PasswordResetToken(String token, LocalDateTime expiryDate, String userEmail) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.userEmail = userEmail;
    }
    
    // ======= Getters and Setters =======
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}