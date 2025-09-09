package com.cabbooking.controller;

import com.cabbooking.model.Driver;
import com.cabbooking.repository.DriverRepository;
import com.cabbooking.service.IDriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.io.IOException;
import java.security.Principal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@WithMockUser(username = "driver1", roles = "DRIVER")
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDriverService driverService;

    @MockBean
    private DriverRepository driverRepository;

    private Driver testDriver;
    private Principal principal;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1);
        testDriver.setUsername("driver1");
        testDriver.setRating(4.5f);
        
        principal = () -> "driver1";
    }

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
    
    @Test
    void removeProfilePhoto_withExistingPhoto_returnsOk() throws Exception {
        when(driverRepository.findByUsername(anyString())).thenReturn(testDriver);
        when(driverService.removeProfilePhoto(anyString())).thenReturn(testDriver);

        mockMvc.perform(delete("/api/drivers/delete-photo")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(driverService, times(1)).removeProfilePhoto("driver1");
    }

    @Test
    void removeProfilePhoto_unauthorizedUser_returnsForbidden() throws Exception {
        when(driverRepository.findByUsername("driver1")).thenReturn(null);

        mockMvc.perform(delete("/api/drivers/delete-photo")
                        .principal(principal))
                .andExpect(status().isForbidden());

        verify(driverService, never()).removeProfilePhoto(anyString());
    }
}