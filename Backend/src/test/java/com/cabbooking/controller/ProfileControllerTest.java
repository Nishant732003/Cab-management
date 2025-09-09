package com.cabbooking.controller;

import com.cabbooking.dto.UserProfileUpdateRequest;
import com.cabbooking.model.AbstractUser;
import com.cabbooking.model.Customer;
import com.cabbooking.service.IProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProfileService profileService;

    private Principal principal;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setUsername("testuser");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
        testCustomer.setMobileNumber("1234567890");

        User userDetails = new User("testuser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        Principal principal = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication((UsernamePasswordAuthenticationToken) principal);
    }

    @Test
    void getUserProfile_validUser_returnsUserProfile() throws Exception {
        when(profileService.getUserProfileByUsername("testuser")).thenReturn(Optional.of(testCustomer));

        mockMvc.perform(get("/api/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Test"));
        verify(profileService, times(1)).getUserProfileByUsername("testuser");
    }

    @Test
    void updateUserProfile_validRequest_returnsUpdatedProfile() throws Exception {
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setMobileNumber("9876543210");
        updateRequest.setAddress("Updated Address");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setUsername("testuser");
        updatedCustomer.setMobileNumber("9876543210");
        updatedCustomer.setAddress("Updated Address");
        
        when(profileService.updateUserProfile("testuser", updateRequest)).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value("9876543210"));
        verify(profileService, times(1)).updateUserProfile("testuser", updateRequest);
    }
}