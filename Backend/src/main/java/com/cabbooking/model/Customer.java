package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Customer Entity representing a customer user in the Cab Booking Platform.
 * Extends AbstractUser to inherit common user properties such as username,
 * password, address, mobile number, and email.
 *
 * This class is mapped as a JPA entity, meaning it will be persisted as a table
 * in the database with fields inherited from AbstractUser.
 *
 * Intended for users who request cab services on the platform.
 */
@Entity
public class Customer extends AbstractUser {

}
