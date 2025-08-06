package com.cabbooking.service;

import com.cabbooking.dto.CustomerRegistrationRequest;
import com.cabbooking.model.Customer;

/**
 * Service interface for customer registration.
 */
public interface CustomerRegistrationService {

    /**
     * Register a new customer.
     * 
     * @param request DTO containing customer registration data
     * @return the created Customer entity
     * @throws IllegalArgumentException if username or email are already taken
     */
    Customer registerCustomer(CustomerRegistrationRequest request);
}
