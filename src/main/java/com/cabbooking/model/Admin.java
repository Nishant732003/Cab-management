package com.cabbooking.model;

import jakarta.persistence.Entity;

/**
 * Admin Entity: Represents administrator users for the Cab Booking Platform.
 *
 * - Extends AbstractUser, so inherits all core user fields (id, username, password, address, etc.).
 * - Mapped as a JPA entity, so will create a dedicated ADMIN table in the DB.
 * - Used for all admin-specific logic, authentication, authorization and privilege management.
 * - You can add admin-only properties here as your system grows, e.g., admin roles, activity logs, etc.
 *
 * Used in:
 *   - AdminRepository: for database access.
 *   - Login, Admin-related services: for authentication, authorization, admin actions.
 *   - DataInitializer: for seeding admin users.
 */
@Entity
public class Admin extends AbstractUser {
    // Place any admin-unique fields here in the future.
    // For now, it is a simple user with elevated privileges.

    // Example for possible extension:
    // private boolean superAdmin;
    // public boolean isSuperAdmin() { return superAdmin; }
    // public void setSuperAdmin(boolean superAdmin) { this.superAdmin = superAdmin; }
}
