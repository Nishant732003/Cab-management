package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Admin Entity:
 *
 * Represents an administrator user in the cab booking platform.
 * Extends AbstractUser to inherit common user properties.
 *
 * Additional fields:
 * - name: The full name of the admin.
 * - verified: Indicates whether this admin has been approved by a superadmin.
 */
@Entity
public class Admin extends AbstractUser {

    // --- REMOVED: This field is now redundant as name is a combination of first and last name from AbstractUser ---
    // private String name;

    /**
     * Indicates if the admin account is verified/activated by the superadmin.
     * Defaults to false for new registrations.
     */
    private Boolean verified = false;

    /**
     * Default constructor.
     */
    public Admin() {
        super();
    }
    
    /**
     * Constructor with all fields for creating an Admin instance.
     * --- UPDATED: New parameters 'firstName' and 'lastName' are added to the constructor. ---
     * --- UPDATED: The super() call now includes 'firstName' and 'lastName'. ---
     */
    public Admin(String username, String password, String address, String mobileNumber, String email, String firstName, String lastName, Boolean verified) {
        super(username, password, address, mobileNumber, email, firstName, lastName);
        this.verified = verified;
    }


    // ===== Getters and Setters =====
    
    // --- REMOVED: No longer need getName() or setName() since firstName and lastName are handled by the superclass ---
    // public String getName() {
    //     return name;
    // }
    // public void setName(String name) {
    //     this.name = name;
    // }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}