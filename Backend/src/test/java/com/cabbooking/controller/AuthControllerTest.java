package com.cabbooking.controller;

import com.cabbooking.dto.*;
import com.cabbooking.model.Admin;
import com.cabbooking.model.Customer;
import com.cabbooking.dto.LoginResponse; // Corrected import
import com.cabbooking.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ILoginService loginService;

    @MockBean
    private ILogoutService logoutService;

    @MockBean
    private IAdminRegistrationService adminRegistrationService;

    @MockBean
    private ICustomerRegistrationService customerRegistrationService;

    @MockBean
    private IDriverRegistrationService driverRegistrationService;

    @MockBean
    private IUserDeletionService userDeletionService;

    @MockBean
    private IVerificationService verificationService;

    @MockBean
    private IPasswordResetService passwordResetService;

    @MockBean
    private IProfileService profileService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        loginResponse = new LoginResponse(
                "Login successful",
                1,
                "customer",
                "test-jwt-token",
                true
        );
    }

    @Test
    void registerAdmin_validRequest_returnsOkAndAdminObject() throws Exception {
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@test.com");
        request.setPassword("password");
        Admin admin = new Admin();
        admin.setId(1);

        when(adminRegistrationService.registerAdmin(any(AdminRegistrationRequest.class))).thenReturn(admin);

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void registerAdmin_duplicateUsername_returnsBadRequest() throws Exception {
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@test.com");
        request.setPassword("password");

        when(adminRegistrationService.registerAdmin(any(AdminRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void registerCustomer_validRequest_returnsOkAndSuccessMessage() throws Exception {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setUsername("customeruser");
        request.setEmail("customer@test.com");
        request.setPassword("password");
        Customer customer = new Customer();
        customer.setId(1);
        when(customerRegistrationService.registerCustomer(any(CustomerRegistrationRequest.class))).thenReturn(customer);

        mockMvc.perform(post("/api/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer registered successfully"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void registerCustomer_invalidRequest_returnsBadRequest() throws Exception {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        // Missing username and password to trigger validation error
        request.setEmail("customer@test.com");
        
        mockMvc.perform(post("/api/auth/register/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerDriver_validRequest_returnsOkAndSuccessMessage() throws Exception {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setUsername("driveruser");
        request.setEmail("driver@test.com");
        request.setPassword("password");
        request.setLicenceNo("LIC123"); // Corrected method name
        // The setCarType method does not exist on the DTO, so it is removed from the test.

        doNothing().when(driverRegistrationService).registerDriver(any(DriverRegistrationRequest.class));

        mockMvc.perform(post("/api/auth/register/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Driver registered successfully, pending admin verification."))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void login_validCredentials_returnsOkAndLoginResponse() throws Exception {
        when(loginService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    void login_invalidCredentials_returnsBadRequest() throws Exception {
        when(loginService.login(any(LoginRequest.class)))
                .thenThrow(new com.cabbooking.exception.AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_validToken_returnsOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
        verify(logoutService, times(1)).blacklistToken("valid-token");
    }

    @Test
    void logout_noToken_returnsOk() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
        verify(logoutService, never()).blacklistToken(any());
    }

    @Test
    void deleteUser_validUsername_returnsOk() throws Exception {
        String username = "testuser";
        doNothing().when(userDeletionService).deleteUser(username);

        mockMvc.perform(delete("/api/auth/delete/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("User with username " + username + " has been deleted."));
    }

    @Test
    void deleteUser_userNotFound_returnsBadRequest() throws Exception {
        String username = "nonexistent";
        doThrow(new IllegalArgumentException("User not found")).when(userDeletionService).deleteUser(username);

        mockMvc.perform(delete("/api/auth/delete/{username}", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    void sendVerificationEmail_validRequest_returnsOk() throws Exception {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("test@test.com");
        doNothing().when(verificationService).sendVerificationLink(any(String.class));

        mockMvc.perform(post("/api/auth/send-verification-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("A verification link has been sent to your email address."));
    }

    @Test
    void verifyEmail_validToken_returnsOk() throws Exception {
        when(verificationService.verifyToken("valid-token")).thenReturn(true);

        mockMvc.perform(get("/api/auth/verify-email").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Your email has been successfully verified!"));
    }

    @Test
    void verifyEmail_invalidToken_returnsBadRequest() throws Exception {
        when(verificationService.verifyToken("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/auth/verify-email").param("token", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The verification link is invalid or has expired."));
    }

    @Test
    void forgotPassword_validRequest_returnsOk() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@test.com");
        doNothing().when(passwordResetService).createAndSendPasswordResetToken(any(String.class));

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("If an account with that email exists, a password reset link has been sent."));
    }

    @Test
    void resetPassword_validSubmission_returnsOk() throws Exception {
        PasswordResetSubmission submission = new PasswordResetSubmission();
        submission.setToken("valid-token");
        submission.setNewPassword("newpassword123");
        when(passwordResetService.resetPassword("valid-token", "newpassword123")).thenReturn(true);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been successfully reset."));
    }

    @Test
    void resetPassword_invalidToken_returnsBadRequest() throws Exception {
        PasswordResetSubmission submission = new PasswordResetSubmission();
        submission.setToken("invalid-token");
        submission.setNewPassword("newpassword123");
        when(passwordResetService.resetPassword("invalid-token", "newpassword123")).thenReturn(false);

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The password reset link is invalid or has expired."));
    }

    @Test
    void checkUsername_existingUsername_returnsTrue() throws Exception {
        String username = "existinguser";
        when(profileService.isUsernameTaken(username)).thenReturn(true);

        mockMvc.perform(get("/api/auth/check/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void checkUsername_nonExistingUsername_returnsFalse() throws Exception {
        String username = "newuser";
        when(profileService.isUsernameTaken(username)).thenReturn(false);

        mockMvc.perform(get("/api/auth/check/username/{username}", username))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}