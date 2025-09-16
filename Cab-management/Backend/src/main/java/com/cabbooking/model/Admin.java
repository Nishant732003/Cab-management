package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Admin entity representing administrative users in the cab booking system.
 */
@Entity
public class Admin extends AbstractUser {

    /**
     * Indicates if the admin account is verified/activated by the superadmin.
     * Defaults to false for new registrations.
     */
    private Boolean verified = false;

    // ===== Getters and Setters =====
    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
