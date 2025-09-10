package com.cabbooking.controller;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Driver;
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AdminController endpoints.
 * Uses MockMvc for simulating HTTP requests and Mockito for mocking service dependencies.
 * Tests include fetching users and verifying drivers.
 */
@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN") // Simulates an authenticated user with ADMIN role
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAdminService adminService;

    @MockBean
    private IAdminVerificationService adminVerificationService;

    private UserSummaryDTO driverSummary, customerSummary;
    private Driver driver;

    /**
     * Initializes sample test data before each test case runs.
     * Creates mock driver and customer DTOs for validation.
     */
    @BeforeEach
    void setUp() {
        driverSummary = new UserSummaryDTO();
        driverSummary.setUserId(1);
        driverSummary.setUsername("driverUser");
        driverSummary.setLicenceNo("LIC123");
        
        customerSummary = new UserSummaryDTO();
        customerSummary.setUserId(2);
        customerSummary.setUsername("customerUser");

        driver = new Driver();
        driver.setId(1);
        driver.setUsername("driverUser");
        driver.setIsAvailable(true);
        driver.setVerified(false);
    }

    /**
     * Test: GET /api/admin/customers
     *
     * Workflow:
     * - Mock adminService.getAllCustomers() to return a single customer DTO
     * - Perform GET request on /api/admin/customers
     * - Expect HTTP 200 OK
     * - Validate that the JSON response contains the expected username
     * - Verify the service method is called once
     */
    @Test
    void getAllCustomers_returnsListOfCustomers() throws Exception {
        List<UserSummaryDTO> customers = Arrays.asList(customerSummary);
        when(adminService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/admin/customers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("customerUser"));

        verify(adminService, times(1)).getAllCustomers();
    }
    
    /**
     * Test: GET /api/admin/drivers
     *
     * Workflow:
     * - Mock adminService.getAllDrivers() to return a single driver DTO
     * - Perform GET request on /api/admin/drivers
     * - Expect HTTP 200 OK
     * - Validate that the JSON response contains the expected driver username
     * - Verify the service method is called once
     */
    @Test
    void getAllDrivers_returnsListOfDrivers() throws Exception {
        List<UserSummaryDTO> drivers = Arrays.asList(driverSummary);
        when(adminService.getAllDrivers()).thenReturn(drivers);

        mockMvc.perform(get("/api/admin/drivers")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("driverUser"));

        verify(adminService, times(1)).getAllDrivers();
    }

    /**
     * Test: PUT /api/admin/verify/driver/{id}
     * Scenario: Valid driver ID is provided
     *
     * Workflow:
     * - Mock adminVerificationService.verifyAdmin() to return a success message
     * - Perform PUT request to verify the driver
     * - Expect HTTP 200 OK
     * - Validate the response content matches the success message
     * - Verify service method is called once
     */
    @Test
    void verifyDriver_validId_returnsOk() throws Exception {
        when(adminVerificationService.verifyAdmin(1)).thenReturn("Admin verified successfully.");

        mockMvc.perform(put("/api/admin/verify/driver/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin verified successfully."));

        verify(adminVerificationService, times(1)).verifyAdmin(1);
    }

    /**
     * Test: PUT /api/admin/verify/driver/{id}
     * Scenario: Invalid driver ID is provided
     *
     * Workflow:
     * - Mock adminVerificationService.verifyAdmin() to throw IllegalArgumentException
     * - Perform PUT request to verify a nonexistent driver
     * - Expect HTTP 400 Bad Request
     * - Validate the response content matches the exception message
     * - Verify service method is called once
     */
    @Test
    void verifyDriver_invalidId_returnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Admin not found"))
                .when(adminVerificationService).verifyAdmin(99);

        mockMvc.perform(put("/api/admin/verify/driver/{id}", 99))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin not found"));

        verify(adminVerificationService, times(1)).verifyAdmin(99);
    }
}
