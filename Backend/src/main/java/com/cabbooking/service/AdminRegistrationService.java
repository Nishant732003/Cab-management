package com.cabbooking.service;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;

public interface AdminRegistrationService {

    /**
     * Registers a new admin with verified=false by default.
     * 
     * @param request registration data
     * @return created Admin entity
     * @throws IllegalArgumentException if username or email is taken
     */
    Admin registerAdmin(AdminRegistrationRequest request);
}
