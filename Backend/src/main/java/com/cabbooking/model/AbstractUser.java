package com.cabbooking.model;

import jakarta.persistence.*;

/**
 * AbstractUser is a base class representing common properties and structure
 * shared by all user entities in the Cab Booking Platform, such as Admin,
 * Customer, and Driver.
 * 
 * This is annotated with @MappedSuperclass, allowing subclasses to inherit
 * its fields and JPA mapping without being a table on its own.
 * 
 * Usage:
 * - Extend this class in concrete user entities (e.g., Admin, Customer, Driver).
 * - Ensures a consistent user model across modules.
 */
@MappedSuperclass
public abstract class AbstractUser {

    /**
     * Unique identifier for every user (primary key, auto-generated).
     * Subclasses will inherit this ID as their table’s primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Username for login; must be unique and not null.
     * Used to identify users in login and business logic.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Password for login; cannot be null.
     * In a real-world app, store hashed, not plain text.
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's full address.
     */
    private String address;

    /**
     * Mobile number of the user.
     */
    private String mobileNumber;

    /**
     * Email address of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    // ====== Getters and Setters =======

    /**
     * Gets the unique user ID (primary key).
     */
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    /**
     * Gets/sets the username.
     */
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    /**
     * Gets/sets the password.
     */
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    /**
     * Gets/sets the address.
     */
    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    /**
     * Gets/sets the mobile number.
     */
    public String getMobileNumber() { return mobileNumber; }

    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    /**
     * Gets/sets the email address.
     */
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
