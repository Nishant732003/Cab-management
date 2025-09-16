package com.cabbooking.service;

import java.util.List;

import com.cabbooking.dto.UserSummaryDTO;

/**
 * Service interface for admin-specific data retrieval operations.
 */
public interface IAdminService {

    /**
     * Retrieves a summary list of all customers.
     *
     * @return A list of UserSummaryDTO for all customers.
     */
    List<UserSummaryDTO> getAllCustomers();

    /**
     * Retrieves a summary list of all drivers.
     *
     * @return A list of UserSummaryDTO for all drivers.
     */
    List<UserSummaryDTO> getAllDrivers();
}
