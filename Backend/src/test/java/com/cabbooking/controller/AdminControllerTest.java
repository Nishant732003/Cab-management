package com.cabbooking.controller;

import com.cabbooking.dto.UserSummaryDTO;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Driver;
import com.cabbooking.model.Customer;
import com.cabbooking.service.IAdminService;
import com.cabbooking.service.IAdminVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN") // Mock a user with ADMIN role for security
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAdminService adminService;

    @MockBean
    private IAdminVerificationService adminVerificationService;

    private UserSummaryDTO driverSummary, customerSummary;
    private Driver driver;

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

    @Test
    void verifyDriver_validId_returnsOk() throws Exception {
        when(adminVerificationService.verifyAdmin(1)).thenReturn("Admin verified successfully.");

        mockMvc.perform(put("/api/admin/verify/driver/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin verified successfully."));
        verify(adminVerificationService, times(1)).verifyAdmin(1);
    }

    @Test
    void verifyDriver_invalidId_returnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Admin not found")).when(adminVerificationService).verifyAdmin(99);

        mockMvc.perform(put("/api/admin/verify/driver/{id}", 99))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Admin not found"));
        verify(adminVerificationService, times(1)).verifyAdmin(99);
    }
}