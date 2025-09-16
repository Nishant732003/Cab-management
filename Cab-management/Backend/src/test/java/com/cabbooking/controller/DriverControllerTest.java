package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.IDriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for DriverController endpoints.
 * Uses MockMvc to simulate HTTP requests and Mockito to mock service layer.
 */
@WebMvcTest(DriverController.class)
@WithMockUser(username = "driver1", roles = "DRIVER") // Mock authenticated driver
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simulates HTTP requests

    @MockBean
    private IDriverService driverService; // Mocked service

    @MockBean
    private DriverRepository driverRepository; // Mocked repository

    private Driver testDriver;
    private Principal principal;

    /**
     * Initializes test data before each test.
     * Sets up a test driver and a principal representing authenticated user.
     */
    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setUsername("driver1");
        testDriver.setRating(4.5f);

        principal = () -> "driver1"; // Mock authenticated principal
    }

    /**
     * Test: POST /api/drivers/upload-photo
     * Scenario: Authenticated driver uploads a valid profile photo
     * Workflow:
     * - Mock repository to return the driver for username
     * - Mock service to return driver after upload
     * - Perform multipart file upload request
     * - Expect HTTP 200 OK
     */
    @Test
    void uploadProfilePhoto_validFile_returnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        when(driverRepository.findByUsername(anyString())).thenReturn(testDriver);
        when(driverService.uploadProfilePhoto(anyString(), any(MockMultipartFile.class))).thenReturn(testDriver);

        mockMvc.perform(multipart("/api/drivers/upload-photo")
                        .file(file)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(driverService, times(1)).uploadProfilePhoto("driver1", file);
    }

    /**
     * Test: POST /api/drivers/upload-photo
     * Scenario: Unauthorized user (not found in repository) tries to upload photo
     * Workflow:
     * - Mock repository to return null
     * - Perform request
     * - Expect HTTP 403 Forbidden
     */
    @Test
    void uploadProfilePhoto_unauthorizedUser_returnsForbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        when(driverRepository.findByUsername("driver1")).thenReturn(null);

        mockMvc.perform(multipart("/api/drivers/upload-photo")
                        .file(file)
                        .principal(principal))
                .andExpect(status().isForbidden());

        verify(driverService, never()).uploadProfilePhoto(anyString(), any(MockMultipartFile.class));
    }

    /**
     * Test: DELETE /api/drivers/delete-photo
     * Scenario: Authenticated driver removes existing profile photo
     * Workflow:
     * - Mock repository to return the driver
     * - Mock service to remove photo
     * - Perform delete request
     * - Expect HTTP 200 OK
     */
    @Test
    void removeProfilePhoto_withExistingPhoto_returnsOk() throws Exception {
        when(driverRepository.findByUsername(anyString())).thenReturn(testDriver);
        when(driverService.removeProfilePhoto(anyString())).thenReturn(testDriver);

        mockMvc.perform(delete("/api/drivers/delete-photo")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(driverService, times(1)).removeProfilePhoto("driver1");
    }

    /**
     * Test: DELETE /api/drivers/delete-photo
     * Scenario: Unauthorized user (not found in repository) tries to remove photo
     * Workflow:
     * - Mock repository to return null
     * - Perform request
     * - Expect HTTP 403 Forbidden
     */
    @Test
    void removeProfilePhoto_unauthorizedUser_returnsForbidden() throws Exception {
        when(driverRepository.findByUsername("driver1")).thenReturn(null);

        mockMvc.perform(delete("/api/drivers/delete-photo")
                        .principal(principal))
                .andExpect(status().isForbidden());

        verify(driverService, never()).removeProfilePhoto(anyString());
    }
}
