package com.cabbooking.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/*
 * VerificationToken entity to manage email verification tokens.
 */
@Entity
public class VerificationToken {

    /*
     * A verification token is a token that is generated when a user registers
     * and is used to verify their email address. It is valid for 24 hours.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The token itself. It's unique and is generated when the user registers.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /*
     * The date and time when the token expires.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /*
     * The email address of the user who registered.
     */
    @Column(nullable = false)
    private String userEmail;

    // --- Constructors ---
    public VerificationToken() {
    }

    public VerificationToken(String token, LocalDateTime expiryDate, String userEmail) {
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
