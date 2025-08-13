package com.cabbooking.service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;

/**
 * ILoginService interface defines the contract for all login-related business logic.
 * 
 * Any implementation of this interface should handle user authentication,
 * validating credentials provided in LoginRequest, and returning an appropriate
 * LoginResponse indicating success or failure.
 * 
 * This abstraction promotes loose coupling, easier testing, and flexibility
 * to swap implementations (e.g., with JWT, OAuth).
 */
public interface ILoginService {

    /**
     * Validates user credentials for authentication.
     *
     * @param request LoginRequest containing username and password provided by the user.
     * @return LoginResponse containing authentication result and user details.
     * @throws AuthenticationException if credentials are invalid (can be a runtime exception).
     */
    LoginResponse login(LoginRequest request);
}
