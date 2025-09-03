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

    /**
     * --- ADDED ---
     * The full name of the admin.
     */
    private String name;

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
     */
    public Admin(String username, String password, String address, String mobileNumber, String email, String name, Boolean verified) {
        super(username, password, address, mobileNumber, email);
        this.name = name;
        this.verified = verified;
    }


    // ===== Getters and Setters =====

    public String getName() {
        return name;
    }

    /**
     * --- FIX ---
     * This method is now correctly implemented to set the admin's name.
     * It accepts a String and assigns it to the 'name' field.
     */
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}