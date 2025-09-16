// package com.cabbooking.controller;

// import com.cabbooking.dto.*;
// import com.cabbooking.model.Admin;
// import com.cabbooking.model.Customer;
// import com.cabbooking.dto.LoginResponse;
// import com.cabbooking.service.*;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// /**
//  * Unit tests for AuthController endpoints.
//  * Uses MockMvc to simulate HTTP requests and Mockito to mock service layer behavior.
//  */
// @WebMvcTest(AuthController.class)
// public class AuthControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean private ILoginService loginService;
//     @MockBean private ILogoutService logoutService;
//     @MockBean private IAdminRegistrationService adminRegistrationService;
//     @MockBean private ICustomerRegistrationService customerRegistrationService;
//     @MockBean private IDriverRegistrationService driverRegistrationService;
//     @MockBean private IUserDeletionService userDeletionService;
//     @MockBean private IVerificationService verificationService;
//     @MockBean private IPasswordResetService passwordResetService;
//     @MockBean private IProfileService profileService;

//     private ObjectMapper objectMapper = new ObjectMapper();
//     private LoginRequest loginRequest;
//     private LoginResponse loginResponse;

//     /**
//      * Initializes common test data before each test case runs.
//      * Sets up login request and response objects.
//      */
//     @BeforeEach
//     void setUp() {
//         loginRequest = new LoginRequest();
//         loginRequest.setUsername("testuser");
//         loginRequest.setPassword("password123");

//         loginResponse = new LoginResponse(
//                 "Login successful",
//                 1,
//                 "customer",
//                 "test-jwt-token",
//                 true
//         );
//     }

//     /**
//      * Test: POST /api/auth/register/admin
//      * Scenario: Valid admin registration request is provided.
//      * Expectation: HTTP 200 OK with Admin object containing ID.
//      */
//     @Test
//     void registerAdmin_validRequest_returnsOkAndAdminObject() throws Exception {
//         AdminRegistrationRequest request = new AdminRegistrationRequest();
//         request.setUsername("adminuser");
//         request.setEmail("admin@test.com");
//         request.setPassword("password");
//         Admin admin = new Admin();
//         admin.setId(1);

//         when(adminRegistrationService.registerAdmin(any(AdminRegistrationRequest.class))).thenReturn(admin);

//         mockMvc.perform(post("/api/auth/register/admin")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1));
//     }

//     /**
//      * Test: POST /api/auth/register/admin
//      * Scenario: Username already exists for admin.
//      * Expectation: HTTP 400 Bad Request with error message.
//      */
//     @Test
//     void registerAdmin_duplicateUsername_returnsBadRequest() throws Exception {
//         AdminRegistrationRequest request = new AdminRegistrationRequest();
//         request.setUsername("adminuser");
//         request.setEmail("admin@test.com");
//         request.setPassword("password");

//         when(adminRegistrationService.registerAdmin(any(AdminRegistrationRequest.class)))
//                 .thenThrow(new IllegalArgumentException("Username already exists"));

//         mockMvc.perform(post("/api/auth/register/admin")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("Username already exists"));
//     }

//     /**
//      * Test: POST /api/auth/register/customer
//      * Scenario: Valid customer registration request is provided.
//      * Expectation: HTTP 200 OK with success message and userId.
//      */
//     @Test
//     void registerCustomer_validRequest_returnsOkAndSuccessMessage() throws Exception {
//         CustomerRegistrationRequest request = new CustomerRegistrationRequest();
//         request.setUsername("customeruser");
//         request.setEmail("customer@test.com");
//         request.setPassword("password");
//         Customer customer = new Customer();
//         customer.setId(1);

//         when(customerRegistrationService.registerCustomer(any(CustomerRegistrationRequest.class))).thenReturn(customer);

//         mockMvc.perform(post("/api/auth/register/customer")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Customer registered successfully"))
//                 .andExpect(jsonPath("$.userId").value(1))
//                 .andExpect(jsonPath("$.success").value(true));
//     }

//     /**
//      * Test: POST /api/auth/register/customer
//      * Scenario: Invalid customer request (missing username & password).
//      * Expectation: HTTP 400 Bad Request due to validation failure.
//      */
//     @Test
//     void registerCustomer_invalidRequest_returnsBadRequest() throws Exception {
//         CustomerRegistrationRequest request = new CustomerRegistrationRequest();
//         request.setEmail("customer@test.com"); // Missing username/password

//         mockMvc.perform(post("/api/auth/register/customer")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isBadRequest());
//     }

//     /**
//      * Test: POST /api/auth/register/driver
//      * Scenario: Valid driver registration request is provided.
//      * Expectation: HTTP 200 OK with success message about pending verification.
//      */
//     @Test
//     void registerDriver_validRequest_returnsOkAndSuccessMessage() throws Exception {
//         DriverRegistrationRequest request = new DriverRegistrationRequest();
//         request.setUsername("driveruser");
//         request.setEmail("driver@test.com");
//         request.setPassword("password");
//         request.setLicenceNo("LIC123");

//         doNothing().when(driverRegistrationService).registerDriver(any(DriverRegistrationRequest.class));

//         mockMvc.perform(post("/api/auth/register/driver")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Driver registered successfully, pending admin verification."))
//                 .andExpect(jsonPath("$.success").value(true));
//     }

//     /**
//      * Test: POST /api/auth/login
//      * Scenario: Valid login credentials are provided.
//      * Expectation: HTTP 200 OK with JWT token in response.
//      */
//     @Test
//     void login_validCredentials_returnsOkAndLoginResponse() throws Exception {
//         when(loginService.login(any(LoginRequest.class))).thenReturn(loginResponse);

//         mockMvc.perform(post("/api/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.token").value("test-jwt-token"));
//     }

//     /**
//      * Test: POST /api/auth/login
//      * Scenario: Invalid login credentials.
//      * Expectation: HTTP 400 Bad Request with error message.
//      */
//     @Test
//     void login_invalidCredentials_returnsBadRequest() throws Exception {
//         when(loginService.login(any(LoginRequest.class)))
//                 .thenThrow(new com.cabbooking.exception.AuthenticationException("Invalid credentials"));

//         mockMvc.perform(post("/api/auth/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isBadRequest());
//     }

//     /**
//      * Test: POST /api/auth/logout
//      * Scenario: Valid token provided in Authorization header.
//      * Expectation: Token is blacklisted and HTTP 200 OK returned.
//      */
//     @Test
//     void logout_validToken_returnsOk() throws Exception {
//         mockMvc.perform(post("/api/auth/logout")
//                         .header("Authorization", "Bearer valid-token"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Logged out successfully"));

//         verify(logoutService, times(1)).blacklistToken("valid-token");
//     }

//     /**
//      * Test: POST /api/auth/logout
//      * Scenario: No token provided in request.
//      * Expectation: HTTP 200 OK but token service not invoked.
//      */
//     @Test
//     void logout_noToken_returnsOk() throws Exception {
//         mockMvc.perform(post("/api/auth/logout"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Logged out successfully"));

//         verify(logoutService, never()).blacklistToken(any());
//     }

//     /**
//      * Test: DELETE /api/auth/delete/{username}
//      * Scenario: Existing user is deleted successfully.
//      * Expectation: HTTP 200 OK with success message.
//      */
//     @Test
//     void deleteUser_validUsername_returnsOk() throws Exception {
//         String username = "testuser";
//         doNothing().when(userDeletionService).deleteUser(username);

//         mockMvc.perform(delete("/api/auth/delete/{username}", username))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("User with username " + username + " has been deleted."));
//     }

//     /**
//      * Test: DELETE /api/auth/delete/{username}
//      * Scenario: User does not exist.
//      * Expectation: HTTP 400 Bad Request with error message.
//      */
//     @Test
//     void deleteUser_userNotFound_returnsBadRequest() throws Exception {
//         String username = "nonexistent";
//         doThrow(new IllegalArgumentException("User not found")).when(userDeletionService).deleteUser(username);

//         mockMvc.perform(delete("/api/auth/delete/{username}", username))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("User not found"));
//     }

//     /**
//      * Test: POST /api/auth/send-verification-email
//      * Scenario: Valid email provided.
//      * Expectation: HTTP 200 OK with success message about verification link.
//      */
//     @Test
//     void sendVerificationEmail_validRequest_returnsOk() throws Exception {
//         EmailVerificationRequest request = new EmailVerificationRequest();
//         request.setEmail("test@test.com");
//         doNothing().when(verificationService).sendVerificationLink(any(String.class));

//         mockMvc.perform(post("/api/auth/send-verification-email")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("A verification link has been sent to your email address."));
//     }

//     /**
//      * Test: GET /api/auth/verify-email
//      * Scenario: Valid token provided for verification.
//      * Expectation: HTTP 200 OK with success message.
//      */
//     @Test
//     void verifyEmail_validToken_returnsOk() throws Exception {
//         when(verificationService.verifyToken("valid-token")).thenReturn(true);

//         mockMvc.perform(get("/api/auth/verify-email").param("token", "valid-token"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Your email has been successfully verified!"));
//     }

//     /**
//      * Test: GET /api/auth/verify-email
//      * Scenario: Invalid or expired token provided.
//      * Expectation: HTTP 400 Bad Request with error message.
//      */
//     @Test
//     void verifyEmail_invalidToken_returnsBadRequest() throws Exception {
//         when(verificationService.verifyToken("invalid-token")).thenReturn(false);

//         mockMvc.perform(get("/api/auth/verify-email").param("token", "invalid-token"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("The verification link is invalid or has expired."));
//     }

//     /**
//      * Test: POST /api/auth/forgot-password
//      * Scenario: Valid email provided for password reset.
//      * Expectation: HTTP 200 OK with message about reset link.
//      */
//     @Test
//     void forgotPassword_validRequest_returnsOk() throws Exception {
//         PasswordResetRequest request = new PasswordResetRequest();
//         request.setEmail("test@test.com");
//         doNothing().when(passwordResetService).createAndSendPasswordResetToken(any(String.class));

//         mockMvc.perform(post("/api/auth/forgot-password")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("If an account with that email exists, a password reset link has been sent."));
//     }

//     /**
//      * Test: POST /api/auth/reset-password
//      * Scenario: Valid token and new password provided.
//      * Expectation: HTTP 200 OK with success message.
//      */
//     @Test
//     void resetPassword_validSubmission_returnsOk() throws Exception {
//         PasswordResetSubmission submission = new PasswordResetSubmission();
//         submission.setToken("valid-token");
//         submission.setNewPassword("newpassword123");

//         when(passwordResetService.resetPassword("valid-token", "newpassword123")).thenReturn(true);

//         mockMvc.perform(post("/api/auth/reset-password")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(submission)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Your password has been successfully reset."));
//     }

//     /**
//      * Test: POST /api/auth/reset-password
//      * Scenario: Invalid or expired reset token.
//      * Expectation: HTTP 400 Bad Request with error message.
//      */
//     @Test
//     void resetPassword_invalidToken_returnsBadRequest() throws Exception {
//         PasswordResetSubmission submission = new PasswordResetSubmission();
//         submission.setToken("invalid-token");
//         submission.setNewPassword("newpassword123");

//         when(passwordResetService.resetPassword("invalid-token", "newpassword123")).thenReturn(false);

//         mockMvc.perform(post("/api/auth/reset-password")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(submission)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("The password reset link is invalid or has expired."));
//     }

//     /**
//      * Test: GET /api/auth/check/username/{username}
//      * Scenario: Username already exists.
//      * Expectation: HTTP 200 OK with "true".
//      */
//     @Test
//     void checkUsername_existingUsername_returnsTrue() throws Exception {
//         String username = "existinguser";
//         when(profileService.isUsernameTaken(username)).thenReturn(true);

//         mockMvc.perform(get("/api/auth/check/username/{username}", username))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("true"));
//     }

//     /**
//      * Test: GET /api/auth/check/username/{username}
//      * Scenario: Username is available.
//      * Expectation: HTTP 200 OK with "false".
//      */
//     @Test
//     void checkUsername_nonExistingUsername_returnsFalse() throws Exception {
//         String username = "newuser";
//         when(profileService.isUsernameTaken(username)).thenReturn(false);

//         mockMvc.perform(get("/api/auth/check/username/{username}", username))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("false"));
//     }
// }
