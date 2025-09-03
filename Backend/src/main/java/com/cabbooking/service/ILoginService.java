package com.cabbooking.service;

import com.cabbooking.dto.LoginRequest;
import com.cabbooking.dto.LoginResponse;

/**
 * Service interface for user authentication.
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
