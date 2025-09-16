package com.cabbooking.service;

import com.cabbooking.dto.DriverRegistrationRequest;
import com.cabbooking.model.Driver;

/**
 * Service interface for driver registration.
 */
public interface IDriverRegistrationService {

    /**
     * Registers a new driver account with unverified status.
     * 
     * @param request Driver registration data
     * @return saved Driver entity
     * @throws IllegalArgumentException if username, email, or license already exists
     */
    Driver registerDriver(DriverRegistrationRequest request);
}
