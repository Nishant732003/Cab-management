package com.cabbooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // The token itself should be unique
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // ==> THIS IS THE KEY CHANGE <==
    // Store the user's email directly. It's unique across the system.
    @Column(nullable = false)
    private String userEmail;

    // --- Constructors ---
    public VerificationToken() {}

    public VerificationToken(String token, LocalDateTime expiryDate, String userEmail) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.userEmail = userEmail;
    }

    // --- Getters and Setters ---
    
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