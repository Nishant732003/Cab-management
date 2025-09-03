package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Admin Entity:
 *
 * Represents an administrator user in the cab booking platform. Extends
 * AbstractUser to inherit common user properties.
 *
 * Additional field: - verified: Indicates whether this admin has been approved
 * by superadmin.
 *
 * The superadmin (preset with username "harshit") should have verified = true.
 */
@Entity
public class Admin extends AbstractUser {

    /**
     * Indicates if the admin account is verified/activated by the superadmin.
     */
    private Boolean verified = false;

    // ======= Getters and Setters =======
    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
